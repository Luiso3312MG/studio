package com.kbseed.service;

import com.kbseed.dto.BulkClassesResponse;
import com.kbseed.dto.ClassDTO;
import com.kbseed.dto.CreateBulkClassesRequest;
import com.kbseed.entity.ClassEntity;
import com.kbseed.entity.ClassTypeEntity;
import com.kbseed.entity.RoomEntity;
import com.kbseed.repository.ClassRepository;
import com.kbseed.repository.ClassTypeRepository;
import com.kbseed.repository.RoomRepository;
import com.kbseed.support.SessionContext;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassTypeRepository classTypeRepository;
    private final RoomRepository roomRepository;
    private final SessionContext sessionContext;

    public ClassService(ClassRepository classRepository,
                        ClassTypeRepository classTypeRepository,
                        RoomRepository roomRepository,
                        SessionContext sessionContext) {
        this.classRepository = classRepository;
        this.classTypeRepository = classTypeRepository;
        this.roomRepository = roomRepository;
        this.sessionContext = sessionContext;
    }

    public List<ClassDTO> obtenerTodas() {
        Long studioId = sessionContext.requireStudioId();
        return classRepository.findByStudioIdOrderByClassDateAscStartTimeAsc(studioId).stream().map(this::toDTO).toList();
    }

    public ClassDTO obtenerPorId(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClassEntity entity = classRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));
        return toDTO(entity);
    }

    public ClassDTO crear(ClassDTO dto) {
        Long studioId = sessionContext.requireStudioId();
        ClassEntity entity = toEntity(dto);
        entity.setId(null);
        entity.setStudioId(studioId);
        ClassTypeEntity classType = classTypeRepository.findByStudioIdAndName(studioId, dto.getClassTypeName())
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado"));
        RoomEntity room = roomRepository.findByStudioId(studioId).stream()
                .filter(item -> item.getName().equals(dto.getRoomName()))
                .findFirst().orElseThrow(() -> new RuntimeException("Salón no encontrado"));
        int finalCapacity = dto.getCapacityTotal() != null ? dto.getCapacityTotal() : classType.getCapacity();
        if (finalCapacity > room.getCapacity()) {
            throw new RuntimeException("La capacidad indicada supera la capacidad del salón/cabina");
        }
        entity.setCapacity(finalCapacity);
        entity.setCapacityTotal(finalCapacity);
        entity.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "DISPONIBLE" : dto.getStatus());
        return toDTO(classRepository.save(entity));
    }

    public ClassDTO actualizar(Long id, ClassDTO dto) {
        Long studioId = sessionContext.requireStudioId();
        ClassEntity entity = classRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));
        entity.setClassTypeName(dto.getClassTypeName());
        entity.setRoomName(dto.getRoomName());
        entity.setCoachUserName(dto.getCoachUserName());
        entity.setClassDate(dto.getClassDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setCapacity(dto.getCapacity());
        entity.setCapacityTotal(dto.getCapacityTotal());
        entity.setStatus(dto.getStatus());
        return toDTO(classRepository.save(entity));
    }

    public void eliminar(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClassEntity entity = classRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));
        classRepository.delete(entity);
    }

    public BulkClassesResponse crearClasesMasivas(CreateBulkClassesRequest request) {
        Long studioId = sessionContext.requireStudioId();
        if (request.getStartDate() == null || request.getEndDate() == null) throw new RuntimeException("Debes indicar fecha inicio y fecha fin");
        if (request.getStartDate().isAfter(request.getEndDate())) throw new RuntimeException("La fecha inicio no puede ser mayor a la fecha fin");
        if (request.getWeekdays() == null || request.getWeekdays().isEmpty()) throw new RuntimeException("Debes seleccionar al menos un día de la semana");

        ClassTypeEntity classType = classTypeRepository.findById(request.getClassTypeId())
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado"));
        RoomEntity room = roomRepository.findById(request.getRoomId())
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Salón no encontrado"));

        int finalCapacity = request.getCapacity() != null ? request.getCapacity() : classType.getCapacity();
        if (finalCapacity > room.getCapacity()) throw new RuntimeException("La capacidad indicada supera la capacidad del salón/cabina");

        Set<Integer> weekdaySet = new HashSet<>(request.getWeekdays());
        List<ClassEntity> classesToSave = new ArrayList<>();
        LocalDate current = request.getStartDate();
        while (!current.isAfter(request.getEndDate())) {
            int jsWeekday = mapDayOfWeekToFrontend(current.getDayOfWeek());
            if (weekdaySet.contains(jsWeekday)) {
                ClassEntity entity = new ClassEntity();
                entity.setStudioId(studioId);
                entity.setClassTypeName(classType.getName());
                entity.setRoomName(room.getName());
                entity.setCoachUserName(request.getCoachUserName());
                entity.setClassDate(current);
                entity.setStartTime(request.getStartTime());
                entity.setEndTime(request.getEndTime());
                entity.setCapacity(finalCapacity);
                entity.setCapacityTotal(finalCapacity);
                entity.setStatus("DISPONIBLE");
                classesToSave.add(entity);
            }
            current = current.plusDays(1);
        }

        List<ClassEntity> saved = classRepository.saveAll(classesToSave);
        BulkClassesResponse response = new BulkClassesResponse();
        response.setTotalCreated(saved.size());
        response.setCreatedIds(saved.stream().map(ClassEntity::getId).toList());
        return response;
    }

    private ClassDTO toDTO(ClassEntity entity) {
        ClassDTO dto = new ClassDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setClassTypeName(entity.getClassTypeName());
        dto.setRoomName(entity.getRoomName());
        dto.setCoachUserName(entity.getCoachUserName());
        dto.setClassDate(entity.getClassDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setCapacity(entity.getCapacity());
        dto.setCapacityTotal(entity.getCapacityTotal());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    private ClassEntity toEntity(ClassDTO dto) {
        ClassEntity entity = new ClassEntity();
        entity.setId(dto.getId());
        entity.setStudioId(dto.getStudioId());
        entity.setClassTypeName(dto.getClassTypeName());
        entity.setRoomName(dto.getRoomName());
        entity.setCoachUserName(dto.getCoachUserName());
        entity.setClassDate(dto.getClassDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setCapacity(dto.getCapacity());
        entity.setCapacityTotal(dto.getCapacityTotal());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private int mapDayOfWeekToFrontend(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 0;
        };
    }
}
