package com.kbseed.service;

import com.kbseed.dto.ClassDTO;
import com.kbseed.entity.ClassEntity;
import com.kbseed.repository.ClassRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
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
}
