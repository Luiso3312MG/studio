package com.kbseed.service;

import com.kbseed.dto.ClassDTO;
import com.kbseed.entity.ClassEntity;
import com.kbseed.repository.ClassRepository;

import com.kbseed.dto.BulkClassesResponse;
import com.kbseed.dto.CreateBulkClassesRequest;
import com.kbseed.entity.ClassTypeEntity;
import com.kbseed.entity.RoomEntity;
import com.kbseed.repository.ClassTypeRepository;
import com.kbseed.repository.RoomRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    private final ClassTypeRepository classTypeRepository;
    private final RoomRepository roomRepository;

    public ClassService(
            ClassRepository classRepository,
            ClassTypeRepository classTypeRepository,
            RoomRepository roomRepository) {
        this.classRepository = classRepository;
        this.classTypeRepository = classTypeRepository;
        this.roomRepository = roomRepository;
    }

    public List<ClassDTO> obtenerTodas() {
        return classRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClassDTO obtenerPorId(Long id) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));
        return toDTO(entity);
    }

    public ClassDTO crear(ClassDTO dto) {
        ClassEntity entity = toEntity(dto);
        ClassEntity saved = classRepository.save(entity);
        return toDTO(saved);
    }

    public ClassDTO actualizar(Long id, ClassDTO dto) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));

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

        ClassEntity updated = classRepository.save(entity);
        return toDTO(updated);
    }

    public void eliminar(Long id) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con id: " + id));

        classRepository.delete(entity);
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

    public BulkClassesResponse crearClasesMasivas(CreateBulkClassesRequest request) {

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RuntimeException("Debes indicar fecha inicio y fecha fin");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("La fecha inicio no puede ser mayor a la fecha fin");
        }

        if (request.getWeekdays() == null || request.getWeekdays().isEmpty()) {
            throw new RuntimeException("Debes seleccionar al menos un día de la semana");
        }

        ClassTypeEntity classType = classTypeRepository.findById(request.getClassTypeId())
                .orElseThrow(
                        () -> new RuntimeException("Tipo de clase no encontrado con id: " + request.getClassTypeId()));

        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + request.getRoomId()));

        if (!classType.getStudioId().equals(request.getStudioId())) {
            throw new RuntimeException("El tipo de clase no pertenece al studio indicado");
        }

        if (!room.getStudioId().equals(request.getStudioId())) {
            throw new RuntimeException("El salón no pertenece al studio indicado");
        }

        int finalCapacity = request.getCapacity() != null ? request.getCapacity() : classType.getCapacity();
        if (finalCapacity > room.getCapacity()) {
            throw new RuntimeException("La capacidad indicada supera la capacidad del salón");
        }

        Set<Integer> weekdaySet = new HashSet<>(request.getWeekdays());
        List<ClassEntity> classesToSave = new ArrayList<>();

        LocalDate current = request.getStartDate();

        while (!current.isAfter(request.getEndDate())) {
            int jsWeekday = mapDayOfWeekToFrontend(current.getDayOfWeek());

            if (weekdaySet.contains(jsWeekday)) {
                ClassEntity entity = new ClassEntity();
                entity.setStudioId(request.getStudioId());
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
