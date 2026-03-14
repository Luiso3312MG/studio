package com.kbseed.service;

import com.kbseed.dto.CreateReservationRequest;
import com.kbseed.dto.ReservationDTO;
import com.kbseed.entity.ClassEntity;
import com.kbseed.entity.ClientEntity;
import com.kbseed.entity.ReservationEntity;
import com.kbseed.repository.ClassRepository;
import com.kbseed.repository.ClientRepository;
import com.kbseed.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClassRepository classRepository;
    private final ClientRepository clientRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ClassRepository classRepository,
            ClientRepository clientRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.classRepository = classRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ReservationDTO inscribirAlumno(Long classId, CreateReservationRequest request) {

        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Clase no encontrada con id: " + classId
                ));

        ClientEntity clientEntity = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alumno no encontrado con id: " + request.getClientId()
                ));

        if (!classEntity.getStudioId().equals(clientEntity.getStudioId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La clase y el alumno no pertenecen al mismo studio"
            );
        }

        boolean yaInscrito = reservationRepository.existsByClassIdAndClientId(classId, request.getClientId());
        if (yaInscrito) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alumno ya está inscrito en esta clase"
            );
        }

        if (classEntity.getCapacity() == null || classEntity.getCapacity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La clase ya no tiene cupos disponibles"
            );
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setStudioId(classEntity.getStudioId());
        reservation.setClassId(classEntity.getId());
        reservation.setClientId(clientEntity.getId());
        reservation.setReservationStatus("RESERVADO");
        reservation.setNotes(request.getNotes());

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        classEntity.setCapacity(classEntity.getCapacity() - 1);
        if (classEntity.getCapacity() <= 0) {
            classEntity.setStatus("LLENA");
        }

        classRepository.save(classEntity);

        return toDTO(savedReservation, clientEntity);
    }

    public List<ReservationDTO> obtenerReservasPorClase(Long classId) {
        classRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Clase no encontrada con id: " + classId
                ));

        List<ReservationEntity> reservations = reservationRepository.findByClassId(classId);

        return reservations.stream()
                .map(reservation -> {
                    ClientEntity client = clientRepository.findById(reservation.getClientId()).orElse(null);
                    return toDTO(reservation, client);
                })
                .collect(Collectors.toList());
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