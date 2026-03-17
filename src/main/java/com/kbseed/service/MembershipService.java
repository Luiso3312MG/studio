package com.kbseed.service;

import com.kbseed.dto.*;
import com.kbseed.entity.*;
import com.kbseed.repository.*;
import com.kbseed.support.SessionContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final ClientMembershipRepository clientMembershipRepository;
    private final MembershipDisciplineRepository membershipDisciplineRepository;
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final ClassTypeRepository classTypeRepository;
    private final ReservationRepository reservationRepository;
    private final ClassRepository classRepository;
    private final SessionContext sessionContext;

    public MembershipService(MembershipPlanRepository membershipPlanRepository,
                             ClientMembershipRepository clientMembershipRepository,
                             MembershipDisciplineRepository membershipDisciplineRepository,
                             PaymentRepository paymentRepository,
                             ClientRepository clientRepository,
                             ClassTypeRepository classTypeRepository,
                             ReservationRepository reservationRepository,
                             ClassRepository classRepository,
                             SessionContext sessionContext) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.clientMembershipRepository = clientMembershipRepository;
        this.membershipDisciplineRepository = membershipDisciplineRepository;
        this.paymentRepository = paymentRepository;
        this.clientRepository = clientRepository;
        this.classTypeRepository = classTypeRepository;
        this.reservationRepository = reservationRepository;
        this.classRepository = classRepository;
        this.sessionContext = sessionContext;
    }

    public List<MembershipPlanDTO> getPlans() {
        Long studioId = sessionContext.requireStudioId();
        return membershipPlanRepository.findByStudioIdAndIsActiveTrueOrderByPriceAsc(studioId)
                .stream().map(this::toPlanDTO).toList();
    }

    public ClientMembershipDTO getCurrentMembershipForClient(Long clientId) {
        Long studioId = sessionContext.requireStudioId();
        ClientEntity client = requireClient(clientId, studioId);
        ClientMembershipEntity membership = clientMembershipRepository.findTopByClientIdOrderByEndDateDesc(client.getId()).orElse(null);
        return membership == null ? null : toMembershipDTO(membership);
    }

    public List<MembershipOverviewDTO> getOverview() {
        Long studioId = sessionContext.requireStudioId();
        List<ClientEntity> clients = clientRepository.findByStudioIdOrderByFirstNameAscLastNameAsc(studioId);
        return clients.stream().map(client -> {
            MembershipOverviewDTO dto = new MembershipOverviewDTO();
            dto.setClientId(client.getId());
            dto.setClientName((client.getFirstName() + " " + Optional.ofNullable(client.getLastName()).orElse("")).trim());
            dto.setClientEmail(client.getEmail());
            dto.setClientPhone(client.getPhone());
            ClientMembershipEntity membership = clientMembershipRepository.findTopByClientIdOrderByEndDateDesc(client.getId()).orElse(null);
            if (membership != null) {
                MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId()).orElse(null);
                dto.setMembershipStatus(resolveMembershipStatus(membership));
                dto.setPlanName(plan != null ? plan.getName() : null);
                dto.setMembershipEndDate(membership.getEndDate());
                dto.setDisciplines(getDisciplineNames(membership));
                dto.setReservationAvailabilityMessage(getDisciplineReservationAvailabilityMessage(membership));
                dto.setRemainingPresotherapySessions(calculateRemainingComplementSessions(membership, "PRESOTERAPIA"));
                dto.setRemainingAparatologySessions(calculateRemainingComplementSessions(membership, "APARATOLOGIA"));
            } else {
                dto.setMembershipStatus("SIN_MEMBRESIA");
                dto.setDisciplines(List.of());
                dto.setReservationAvailabilityMessage("No cuenta con membresía activa");
                dto.setRemainingPresotherapySessions(0);
                dto.setRemainingAparatologySessions(0);
            }
            return dto;
        }).toList();
    }

    @Transactional
    public ClientMembershipDTO activateOrRenew(Long clientId, ActivateMembershipRequest request) {
        Long studioId = sessionContext.requireStudioId();
        ClientEntity client = requireClient(clientId, studioId);
        MembershipPlanEntity plan = membershipPlanRepository.findById(request.getPlanId())
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado"));

        if (request.getAmount() == null || request.getAmount().compareTo(plan.getPrice()) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago debe coincidir con el precio del plan");
        }

        validateDisciplines(studioId, plan, request.getDisciplineIds());

        ClientMembershipEntity previous = clientMembershipRepository.findTopByClientIdOrderByEndDateDesc(clientId).orElse(null);
        LocalDate startDate = LocalDate.now(ZoneId.of("America/Mexico_City"));
        if (previous != null && previous.getEndDate() != null && previous.getEndDate().isAfter(LocalDate.now(ZoneId.of("America/Mexico_City")))) {
            startDate = previous.getEndDate().plusDays(1);
            previous.setStatus("RENOVADA");
            clientMembershipRepository.save(previous);
        } else if (previous != null && previous.getEndDate() != null && previous.getEndDate().isBefore(LocalDate.now(ZoneId.of("America/Mexico_City")))) {
            previous.setStatus("VENCIDA");
            clientMembershipRepository.save(previous);
        }

        ClientMembershipEntity membership = new ClientMembershipEntity();
        membership.setStudioId(studioId);
        membership.setClientId(client.getId());
        membership.setMembershipPlanId(plan.getId());
        membership.setStartDate(startDate);
        membership.setEndDate(startDate.plusDays(plan.getDaysDuration() - 1L));
        membership.setStatus("ACTIVA");
        membership.setPricePaid(request.getAmount());
        membership = clientMembershipRepository.save(membership);

        replaceDisciplines(membership.getId(), request.getDisciplineIds());

        PaymentEntity payment = new PaymentEntity();
        payment.setStudioId(studioId);
        payment.setClientId(client.getId());
        payment.setClientMembershipId(membership.getId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod() == null || request.getPaymentMethod().isBlank() ? "EFECTIVO" : request.getPaymentMethod());
        payment.setPaymentDate(LocalDate.now(ZoneId.of("America/Mexico_City")));
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());
        payment.setPaymentType("MEMBRESIA");
        paymentRepository.save(payment);

        return toMembershipDTO(membership);
    }

    @Transactional
    public ClientMembershipDTO updateDisciplines(Long membershipId, UpdateMembershipDisciplinesRequest request) {
        Long studioId = sessionContext.requireStudioId();
        ClientMembershipEntity membership = clientMembershipRepository.findById(membershipId)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membresía no encontrada"));
        MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado"));
        validateDisciplines(studioId, plan, request.getDisciplineIds());
        replaceDisciplines(membership.getId(), request.getDisciplineIds());
        return toMembershipDTO(membership);
    }

    public ClientMembershipEntity requireActiveMembership(Long clientId, Long studioId) {
        ClientMembershipEntity membership = clientMembershipRepository
                .findTopByClientIdAndStatusAndEndDateGreaterThanEqualOrderByEndDateDesc(clientId, "ACTIVA", LocalDate.now(ZoneId.of("America/Mexico_City")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "El alumno no tiene una membresía activa"));
        if (!membership.getStudioId().equals(studioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El alumno no pertenece al studio actual");
        }
        return membership;
    }

    public boolean isDisciplineAllowed(ClientMembershipEntity membership, ClassTypeEntity classType) {
        MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId()).orElseThrow();
        if (Boolean.TRUE.equals(plan.getAllowsAllDisciplines())) return true;
        List<Long> disciplineIds = membershipDisciplineRepository.findByClientMembershipId(membership.getId())
                .stream().map(MembershipDisciplineEntity::getClassTypeId).toList();
        return disciplineIds.contains(classType.getId());
    }

    public int calculateRemainingComplementSessions(ClientMembershipEntity membership, String serviceKind) {
        MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId()).orElseThrow();
        int total = switch (serviceKind) {
            case "PRESOTERAPIA" -> Optional.ofNullable(plan.getPresotherapySessions()).orElse(0);
            case "APARATOLOGIA" -> Optional.ofNullable(plan.getAparatologySessions()).orElse(0);
            default -> 0;
        };
        if (total <= 0) return 0;

        List<ReservationEntity> reservations = reservationRepository.findByClientIdAndReservationStatusIn(
                membership.getClientId(), List.of("RESERVADO", "ASISTIO", "NO_ASISTIO"));
        if (reservations.isEmpty()) return total;
        Map<Long, ClassEntity> classMap = classRepository.findByIdIn(reservations.stream().map(ReservationEntity::getClassId).toList())
                .stream().collect(Collectors.toMap(ClassEntity::getId, Function.identity()));
        Map<String, ClassTypeEntity> typeMap = classTypeRepository.findByStudioId(membership.getStudioId()).stream()
                .collect(Collectors.toMap(ClassTypeEntity::getName, Function.identity(), (a,b) -> a));

        long used = reservations.stream().filter(reservation -> {
            ClassEntity classEntity = classMap.get(reservation.getClassId());
            if (classEntity == null) return false;
            if (classEntity.getClassDate().isBefore(membership.getStartDate()) || classEntity.getClassDate().isAfter(membership.getEndDate())) return false;
            ClassTypeEntity type = typeMap.get(classEntity.getClassTypeName());
            return type != null && serviceKind.equals(type.getServiceKind()) && "COMPLEMENTO".equals(type.getCategory());
        }).count();
        return Math.max(0, total - (int) used);
    }

    public String getDisciplineReservationAvailabilityMessage(ClientMembershipEntity membership) {
        if (membership == null) {
            return "No cuenta con membresía activa";
        }
        if (membership.getEndDate().isBefore(LocalDate.now(ZoneId.of("America/Mexico_City")))) {
            return "La membresía está vencida";
        }
        List<ReservationEntity> existingReservations = reservationRepository.findByClientIdAndReservationStatusIn(
                membership.getClientId(), List.of("RESERVADO", "ASISTIO", "NO_ASISTIO"));
        if (existingReservations.isEmpty()) {
            return "Puede reservar una clase de disciplina hoy";
        }
        Map<Long, ClassEntity> classesById = classRepository.findByIdIn(existingReservations.stream().map(ReservationEntity::getClassId).toList())
                .stream().collect(Collectors.toMap(ClassEntity::getId, Function.identity()));
        Map<String, ClassTypeEntity> typesByName = classTypeRepository.findByStudioId(membership.getStudioId()).stream()
                .collect(Collectors.toMap(ClassTypeEntity::getName, Function.identity(), (a, b) -> a));
        boolean hasDisciplineToday = existingReservations.stream().anyMatch(existing -> {
            ClassEntity classEntity = classesById.get(existing.getClassId());
            if (classEntity == null || !classEntity.getClassDate().equals(LocalDate.now(ZoneId.of("America/Mexico_City")))) return false;
            ClassTypeEntity classType = typesByName.get(classEntity.getClassTypeName());
            return classType != null && "DISCIPLINA".equals(classType.getCategory());
        });
        return hasDisciplineToday ? "Ya tiene una clase activa, cursada o no asistida para hoy" : "Puede reservar una clase de disciplina hoy";
    }

    public boolean canReserveDisciplineToday(ClientMembershipEntity membership) {
        return "Puede reservar una clase de disciplina hoy".equals(getDisciplineReservationAvailabilityMessage(membership));
    }

    public List<String> getDisciplineNames(ClientMembershipEntity membership) {
        Map<Long, ClassTypeEntity> typeMap = classTypeRepository.findByStudioId(membership.getStudioId()).stream()
                .collect(Collectors.toMap(ClassTypeEntity::getId, Function.identity()));
        return membershipDisciplineRepository.findByClientMembershipId(membership.getId()).stream()
                .map(item -> typeMap.get(item.getClassTypeId()))
                .filter(Objects::nonNull)
                .map(ClassTypeEntity::getName)
                .toList();
    }

    private ClientEntity requireClient(Long clientId, Long studioId) {
        return clientRepository.findById(clientId)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
    }

    private void validateDisciplines(Long studioId, MembershipPlanEntity plan, List<Long> disciplineIds) {
        List<Long> ids = disciplineIds == null ? List.of() : disciplineIds;
        if (Boolean.TRUE.equals(plan.getAllowsAllDisciplines())) return;
        if (ids.size() != plan.getDisciplineLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debes seleccionar exactamente " + plan.getDisciplineLimit() + " disciplina(s)");
        }
        Map<Long, ClassTypeEntity> typeMap = classTypeRepository.findByStudioIdAndCategoryAndIsActiveTrue(studioId, "DISCIPLINA")
                .stream().collect(Collectors.toMap(ClassTypeEntity::getId, Function.identity()));
        for (Long disciplineId : ids) {
            if (!typeMap.containsKey(disciplineId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disciplina no válida para este studio");
            }
        }
    }

    private void replaceDisciplines(Long membershipId, List<Long> disciplineIds) {
        membershipDisciplineRepository.deleteByClientMembershipId(membershipId);
        if (disciplineIds == null) return;
        for (Long disciplineId : disciplineIds) {
            MembershipDisciplineEntity entity = new MembershipDisciplineEntity();
            entity.setClientMembershipId(membershipId);
            entity.setClassTypeId(disciplineId);
            membershipDisciplineRepository.save(entity);
        }
    }

    private ClientMembershipDTO toMembershipDTO(ClientMembershipEntity membership) {
        MembershipPlanEntity plan = membershipPlanRepository.findById(membership.getMembershipPlanId()).orElse(null);
        ClientMembershipDTO dto = new ClientMembershipDTO();
        dto.setId(membership.getId());
        dto.setClientId(membership.getClientId());
        dto.setMembershipPlanId(membership.getMembershipPlanId());
        dto.setMembershipPlanName(plan != null ? plan.getName() : null);
        dto.setStatus(resolveMembershipStatus(membership));
        dto.setStartDate(membership.getStartDate());
        dto.setEndDate(membership.getEndDate());
        dto.setPricePaid(membership.getPricePaid());
        List<MembershipDisciplineEntity> disciplines = membershipDisciplineRepository.findByClientMembershipId(membership.getId());
        dto.setDisciplineIds(disciplines.stream().map(MembershipDisciplineEntity::getClassTypeId).toList());
        dto.setDisciplineNames(getDisciplineNames(membership));
        dto.setRemainingPresotherapySessions(calculateRemainingComplementSessions(membership, "PRESOTERAPIA"));
        dto.setRemainingAparatologySessions(calculateRemainingComplementSessions(membership, "APARATOLOGIA"));
        dto.setCanReserveDisciplineToday(canReserveDisciplineToday(membership));
        dto.setReservationAvailabilityMessage(getDisciplineReservationAvailabilityMessage(membership));
        return dto;
    }

    private MembershipPlanDTO toPlanDTO(MembershipPlanEntity entity) {
        MembershipPlanDTO dto = new MembershipPlanDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setName(entity.getName());
        dto.setPlanCode(entity.getPlanCode());
        dto.setDisciplineLimit(entity.getDisciplineLimit());
        dto.setAllowsAllDisciplines(entity.getAllowsAllDisciplines());
        dto.setDaysDuration(entity.getDaysDuration());
        dto.setIncludesComplement(entity.getIncludesComplement());
        dto.setPresotherapySessions(entity.getPresotherapySessions());
        dto.setAparatologySessions(entity.getAparatologySessions());
        dto.setPrice(entity.getPrice());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    private String resolveMembershipStatus(ClientMembershipEntity membership) {
        if (membership.getEndDate().isBefore(LocalDate.now(ZoneId.of("America/Mexico_City")))) {
            return "VENCIDA";
        }
        return membership.getStatus();
    }
}
