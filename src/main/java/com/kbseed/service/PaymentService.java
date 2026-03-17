package com.kbseed.service;

import com.kbseed.dto.DropInPaymentRequest;
import com.kbseed.dto.PaymentDTO;
import com.kbseed.entity.*;
import com.kbseed.repository.*;
import com.kbseed.support.SessionContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final ClassRepository classRepository;
    private final ReservationRepository reservationRepository;
    private final SessionContext sessionContext;

    public PaymentService(PaymentRepository paymentRepository,
                          ClientRepository clientRepository,
                          ClassRepository classRepository,
                          ReservationRepository reservationRepository,
                          SessionContext sessionContext) {
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
        this.classRepository = classRepository;
        this.reservationRepository = reservationRepository;
        this.sessionContext = sessionContext;
    }

    public List<PaymentDTO> listarPagos() {
        Long studioId = sessionContext.requireStudioId();
        return paymentRepository.findByStudioIdOrderByCreatedAtDesc(studioId)
                .stream().map(this::toDTO).toList();
    }

    public List<PaymentDTO> listarPagosPorCliente(Long clientId) {
        Long studioId = sessionContext.requireStudioId();
        requireClient(clientId, studioId);
        return paymentRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public PaymentDTO registrarDropIn(DropInPaymentRequest request) {
        Long studioId = sessionContext.requireStudioId();

        if (request.getClientId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere clientId");
        if (request.getClassId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere classId");
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor a cero");

        ClientEntity client = requireClient(request.getClientId(), studioId);

        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .filter(c -> c.getStudioId().equals(studioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clase no encontrada"));

        // Verificar que la clase no haya finalizado
        ZoneId zonaEstudio = ZoneId.of("America/Mexico_City");
        LocalDateTime classEnd = LocalDateTime.of(classEntity.getClassDate(), classEntity.getEndTime());
        if (classEnd.isBefore(LocalDateTime.now(zonaEstudio)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede registrar drop-in en una clase ya finalizada");

        // Verificar que haya cupo disponible
        if (classEntity.getCapacity() == null || classEntity.getCapacity() <= 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La clase ya no tiene cupos disponibles");

        // Verificar que no tenga reserva activa previa en la misma clase
        List<String> activeStatuses = List.of("RESERVADO", "ASISTIO", "NO_ASISTIO");
        boolean yaReservado = reservationRepository
                .existsByClassIdAndClientIdAndReservationStatusIn(classEntity.getId(), client.getId(), activeStatuses);
        if (yaReservado)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno ya tiene una inscripción activa en esta clase");

        // Registrar pago
        PaymentEntity payment = new PaymentEntity();
        payment.setStudioId(studioId);
        payment.setClientId(client.getId());
        payment.setClassId(classEntity.getId());
        payment.setPaymentType("DROP_IN");
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(
                request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()
                        ? "EFECTIVO" : request.getPaymentMethod());
        payment.setPaymentDate(LocalDate.now(ZoneId.of("America/Mexico_City")));
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());
        payment = paymentRepository.save(payment);

        // Crear reservación automática
        ReservationEntity reservation = new ReservationEntity();
        reservation.setStudioId(studioId);
        reservation.setClassId(classEntity.getId());
        reservation.setClientId(client.getId());
        reservation.setReservationStatus("RESERVADO");
        reservation.setNotes("Drop-in — pago ref: " +
                Optional.ofNullable(request.getReference()).orElse(payment.getId().toString()));
        reservationRepository.save(reservation);

        // Descontar cupo
        classEntity.setCapacity(classEntity.getCapacity() - 1);
        if (classEntity.getCapacity() <= 0) classEntity.setStatus("LLENA");
        classRepository.save(classEntity);

        return toDTO(payment);
    }

    private ClientEntity requireClient(Long clientId, Long studioId) {
        return clientRepository.findById(clientId)
                .filter(c -> c.getStudioId().equals(studioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
    }

    private PaymentDTO toDTO(PaymentEntity entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setClientId(entity.getClientId());
        dto.setClientMembershipId(entity.getClientMembershipId());
        dto.setClassId(entity.getClassId());
        dto.setPaymentType(entity.getPaymentType());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setReference(entity.getReference());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());

        // Enriquecer con nombre del alumno
        clientRepository.findById(entity.getClientId()).ifPresent(c -> {
            String name = c.getFirstName() +
                    (c.getLastName() != null ? " " + c.getLastName() : "");
            dto.setClientName(name.trim());
        });

        // Enriquecer con info de la clase (solo para drop-in)
        if (entity.getClassId() != null) {
            classRepository.findById(entity.getClassId()).ifPresent(cl -> {
                dto.setClassTypeName(cl.getClassTypeName());
                dto.setClassDate(cl.getClassDate() != null ? cl.getClassDate().toString() : null);
            });
        }

        return dto;
    }
}
