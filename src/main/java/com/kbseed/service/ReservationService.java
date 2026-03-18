package com.kbseed.service;

import com.kbseed.dto.CreateReservationRequest;
import com.kbseed.dto.ReservationDTO;
import com.kbseed.entity.*;
import com.kbseed.repository.ClassRepository;
import com.kbseed.repository.ClassTypeRepository;
import com.kbseed.repository.ClientRepository;
import com.kbseed.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final List<String> ACTIVE_STATUSES = List.of("RESERVADO", "ASISTIO", "NO_ASISTIO");

    private final ReservationRepository reservationRepository;
    private final ClassRepository classRepository;
    private final ClientRepository clientRepository;
    private final ClassTypeRepository classTypeRepository;
    private final MembershipService membershipService;
    private final AppSettingService appSettingService;

    public ReservationService(ReservationRepository reservationRepository,
                              ClassRepository classRepository,
                              ClientRepository clientRepository,
                              ClassTypeRepository classTypeRepository,
                              MembershipService membershipService,
                              AppSettingService appSettingService) {
        this.reservationRepository = reservationRepository;
        this.classRepository = classRepository;
        this.clientRepository = clientRepository;
        this.classTypeRepository = classTypeRepository;
        this.membershipService = membershipService;
        this.appSettingService = appSettingService;
    }

    @Transactional
    public ReservationDTO inscribirAlumno(Long classId, CreateReservationRequest request) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clase no encontrada con id: " + classId));
        ClientEntity clientEntity = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado con id: " + request.getClientId()));

        if (!classEntity.getStudioId().equals(clientEntity.getStudioId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clase y el alumno no pertenecen al mismo studio");
        }

        ClassTypeEntity classType = classTypeRepository.findByStudioIdAndName(classEntity.getStudioId(), classEntity.getClassTypeName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de clase no encontrado"));

        LocalDateTime classStart = LocalDateTime.of(classEntity.getClassDate(), classEntity.getStartTime());
        if (!classStart.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede inscribir en clases anteriores o que ya iniciaron");
        }

        ClientMembershipEntity membership = membershipService.requireActiveMembership(clientEntity.getId(), classEntity.getStudioId());
        if (classEntity.getClassDate().isBefore(membership.getStartDate()) || classEntity.getClassDate().isAfter(membership.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La clase está fuera de la vigencia de la membresía activa");
        }

        if (reservationRepository.existsByClassIdAndClientIdAndReservationStatusIn(classId, request.getClientId(), ACTIVE_STATUSES)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno ya está inscrito en esta clase");
        }

        if (classEntity.getCapacity() == null || classEntity.getCapacity() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La clase ya no tiene cupos disponibles");
        }

        validateMembershipRules(clientEntity, classEntity, classType, membership);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setStudioId(classEntity.getStudioId());
        reservation.setClassId(classEntity.getId());
        reservation.setClientId(clientEntity.getId());
        reservation.setReservationStatus("RESERVADO");
        reservation.setNotes(request.getNotes());

        ReservationEntity savedReservation = reservationRepository.save(reservation);
        classEntity.setCapacity(classEntity.getCapacity() - 1);
        if (classEntity.getCapacity() <= 0) classEntity.setStatus("LLENA");
        classRepository.save(classEntity);
        return toDTO(savedReservation, clientEntity);
    }

    public List<ReservationDTO> obtenerReservasPorClase(Long classId) {
        classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clase no encontrada con id: " + classId));
        return reservationRepository.findByClassId(classId).stream().map(reservation -> {
            ClientEntity client = clientRepository.findById(reservation.getClientId()).orElse(null);
            return toDTO(reservation, client);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ReservationDTO cancelarReserva(Long reservationId, String notes) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        ClassEntity classEntity = classRepository.findById(reservation.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clase no encontrada"));
        if (!"RESERVADO".equals(reservation.getReservationStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden cancelar reservas activas");
        }

        LocalDateTime classStart = LocalDateTime.of(classEntity.getClassDate(), classEntity.getStartTime());
        LocalDateTime cancelLimit = classStart.minusMinutes(appSettingService.getReservationCancelMinutes());
        if (LocalDateTime.now().isAfter(cancelLimit)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cancelación debe hacerse al menos " + appSettingService.getReservationCancelMinutes() + " minutos antes");
        }

        reservation.setReservationStatus("CANCELADA");
        reservation.setCancellationAt(LocalDateTime.now());
        if (notes != null && !notes.isBlank()) reservation.setNotes(notes);
        reservation = reservationRepository.save(reservation);

        classEntity.setCapacity(Math.min(
                Optional.ofNullable(classEntity.getCapacityTotal()).orElse(classEntity.getCapacity() + 1),
                Optional.ofNullable(classEntity.getCapacity()).orElse(0) + 1));
        classEntity.setStatus("DISPONIBLE");
        classRepository.save(classEntity);

        ClientEntity client = clientRepository.findById(reservation.getClientId()).orElse(null);
        return toDTO(reservation, client);
    }

    @Transactional
    public ReservationDTO checkInReserva(Long reservationId, String notes) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        if (!"RESERVADO".equals(reservation.getReservationStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden hacer check-in a reservas activas");
        }
        reservation.setReservationStatus("ASISTIO");
        reservation.setCheckedInAt(LocalDateTime.now());
        if (notes != null && !notes.isBlank()) reservation.setNotes(notes);
        reservation = reservationRepository.save(reservation);
        ClientEntity client = clientRepository.findById(reservation.getClientId()).orElse(null);
        return toDTO(reservation, client);
    }

    @Transactional
    public ReservationDTO marcarNoAsistio(Long reservationId, String notes) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
        if (!"RESERVADO".equals(reservation.getReservationStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden marcar reservas activas");
        }
        reservation.setReservationStatus("NO_ASISTIO");
        if (notes != null && !notes.isBlank()) reservation.setNotes(notes);
        reservation = reservationRepository.save(reservation);
        ClientEntity client = clientRepository.findById(reservation.getClientId()).orElse(null);
        return toDTO(reservation, client);
    }

    private void validateMembershipRules(ClientEntity clientEntity,
                                         ClassEntity classEntity,
                                         ClassTypeEntity classType,
                                         ClientMembershipEntity membership) {
        List<ReservationEntity> existingReservations = reservationRepository.findByClientIdAndReservationStatusIn(
                clientEntity.getId(), ACTIVE_STATUSES);
        if (existingReservations.isEmpty()) {
            if ("DISCIPLINA".equals(classType.getCategory()) && !membershipService.isDisciplineAllowed(membership, classType)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La disciplina no está permitida por la membresía activa");
            }
            validateComplementBalance(membership, classType);
            return;
        }

        Map<Long, ClassEntity> classesById = classRepository.findByIdIn(existingReservations.stream().map(ReservationEntity::getClassId).toList())
                .stream().collect(Collectors.toMap(ClassEntity::getId, Function.identity()));
        Map<String, ClassTypeEntity> typesByName = classTypeRepository.findByStudioId(classEntity.getStudioId()).stream()
                .collect(Collectors.toMap(ClassTypeEntity::getName, Function.identity(), (a, b) -> a));

        if ("DISCIPLINA".equals(classType.getCategory())) {
            boolean hasDisciplineOnSameDay = existingReservations.stream().anyMatch(existing -> {
                ClassEntity existingClass = classesById.get(existing.getClassId());
                if (existingClass == null || !existingClass.getClassDate().equals(classEntity.getClassDate())) return false;
                ClassTypeEntity existingType = typesByName.get(existingClass.getClassTypeName());
                return existingType != null && "DISCIPLINA".equals(existingType.getCategory());
            });
            if (hasDisciplineOnSameDay) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El alumno ya tiene una clase del día reservada, cursada o marcada como no asistida");
            }
            if (!membershipService.isDisciplineAllowed(membership, classType)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "La disciplina no está permitida por la membresía activa");
            }
        } else if ("COMPLEMENTO".equals(classType.getCategory())) {
            validateComplementBalance(membership, classType);
        }
    }

    private void validateComplementBalance(ClientMembershipEntity membership, ClassTypeEntity classType) {
        if (!"COMPLEMENTO".equals(classType.getCategory())) return;
        if ("PRESOTERAPIA".equals(classType.getServiceKind())
                && membershipService.calculateRemainingComplementSessions(membership, "PRESOTERAPIA") <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La membresía ya no tiene sesiones de presoterapia disponibles");
        }
        if ("APARATOLOGIA".equals(classType.getServiceKind())
                && membershipService.calculateRemainingComplementSessions(membership, "APARATOLOGIA") <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La membresía ya no tiene sesiones de aparatología disponibles");
        }
    }

    private ReservationDTO toDTO(ReservationEntity entity, ClientEntity client) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setClassId(entity.getClassId());
        dto.setClientId(entity.getClientId());
        dto.setReservationStatus(entity.getReservationStatus());
        dto.setBookedAt(entity.getBookedAt());
        dto.setCheckedInAt(entity.getCheckedInAt());
        dto.setCancellationAt(entity.getCancellationAt());
        dto.setNotes(entity.getNotes());
        if (client != null) {
            dto.setClientFirstName(client.getFirstName());
            dto.setClientLastName(client.getLastName());
            dto.setClientEmail(client.getEmail());
            dto.setClientPhone(client.getPhone());
        }
        return dto;
    }
}
