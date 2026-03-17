package com.kbseed.service;

import com.kbseed.dto.ClientDTO;
import com.kbseed.entity.ClientEntity;
import com.kbseed.entity.ClientMembershipEntity;
import com.kbseed.entity.MembershipPlanEntity;
import com.kbseed.repository.ClientMembershipRepository;
import com.kbseed.repository.ClientRepository;
import com.kbseed.repository.MembershipPlanRepository;
import com.kbseed.support.SessionContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMembershipRepository clientMembershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final SessionContext sessionContext;

    public ClientService(ClientRepository clientRepository,
                         ClientMembershipRepository clientMembershipRepository,
                         MembershipPlanRepository membershipPlanRepository,
                         SessionContext sessionContext) {
        this.clientRepository = clientRepository;
        this.clientMembershipRepository = clientMembershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.sessionContext = sessionContext;
    }

    public List<ClientDTO> obtenerTodos() {
        Long studioId = sessionContext.requireStudioId();
        return clientRepository.findByStudioIdOrderByFirstNameAscLastNameAsc(studioId).stream().map(this::toDTO).toList();
    }

    public ClientDTO obtenerPorId(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClientEntity entity = clientRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con id: " + id));
        return toDTO(entity);
    }

    public ClientDTO crear(ClientDTO dto) {
        ClientEntity entity = toEntity(dto);
        entity.setId(null);
        entity.setStudioId(sessionContext.requireStudioId());
        if (entity.getStatus() == null || entity.getStatus().isBlank()) entity.setStatus("ACTIVO");
        return toDTO(clientRepository.save(entity));
    }

    public ClientDTO actualizar(Long id, ClientDTO dto) {
        Long studioId = sessionContext.requireStudioId();
        ClientEntity entity = clientRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con id: " + id));
        entity.setUserId(dto.getUserId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmergencyContactName(dto.getEmergencyContactName());
        entity.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        entity.setNotes(dto.getNotes());
        entity.setStatus(dto.getStatus());
        return toDTO(clientRepository.save(entity));
    }

    public void eliminar(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClientEntity entity = clientRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con id: " + id));
        clientRepository.delete(entity);
    }

    private ClientDTO toDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setUserId(entity.getUserId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setBirthDate(entity.getBirthDate());
        dto.setEmergencyContactName(entity.getEmergencyContactName());
        dto.setEmergencyContactPhone(entity.getEmergencyContactPhone());
        dto.setNotes(entity.getNotes());
        dto.setStatus(entity.getStatus());

        ClientMembershipEntity membership = clientMembershipRepository.findTopByClientIdOrderByEndDateDesc(entity.getId()).orElse(null);
        if (membership != null) {
            dto.setMembershipStatus(membership.getEndDate().isBefore(LocalDate.now(ZoneId.of("America/Mexico_City"))) ? "VENCIDA" : membership.getStatus());
            dto.setMembershipEndDate(membership.getEndDate());
            MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId()).orElse(null);
            dto.setMembershipPlanName(plan != null ? plan.getName() : null);
        }
        return dto;
    }

    private ClientEntity toEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        entity.setId(dto.getId());
        entity.setStudioId(dto.getStudioId());
        entity.setUserId(dto.getUserId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmergencyContactName(dto.getEmergencyContactName());
        entity.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        entity.setNotes(dto.getNotes());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
