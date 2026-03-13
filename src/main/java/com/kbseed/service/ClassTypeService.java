package com.kbseed.service;

import com.kbseed.dto.ClassTypeDTO;
import com.kbseed.entity.ClassTypeEntity;
import com.kbseed.repository.ClassTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassTypeService {

    private final ClassTypeRepository classTypeRepository;

    public ClassTypeService(ClassTypeRepository classTypeRepository) {
        this.classTypeRepository = classTypeRepository;
    }

    public List<ClassTypeDTO> obtenerTodos() {
        return classTypeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClassTypeDTO> obtenerPorStudioId(Long studioId) {
        return classTypeRepository.findByStudioId(studioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClassTypeDTO obtenerPorId(Long id) {
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado con id: " + id));
        return toDTO(entity);
    }

    public ClassTypeDTO crear(ClassTypeDTO dto) {
        ClassTypeEntity entity = toEntity(dto);
        entity.setId(null);

        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }

        if (entity.getLevel() == null || entity.getLevel().isBlank()) {
            entity.setLevel("ALL");
        }

        ClassTypeEntity saved = classTypeRepository.save(entity);
        return toDTO(saved);
    }

    public ClassTypeDTO actualizar(Long id, ClassTypeDTO dto) {
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado con id: " + id));

        entity.setStudioId(dto.getStudioId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setCapacity(dto.getCapacity());
        entity.setLevel(dto.getLevel());
        entity.setIsActive(dto.getIsActive());

        ClassTypeEntity updated = classTypeRepository.save(entity);
        return toDTO(updated);
    }

    public void eliminar(Long id) {
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado con id: " + id));

        classTypeRepository.delete(entity);
    }

    private ClassTypeDTO toDTO(ClassTypeEntity entity) {
        ClassTypeDTO dto = new ClassTypeDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setCapacity(entity.getCapacity());
        dto.setLevel(entity.getLevel());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    private ClassTypeEntity toEntity(ClassTypeDTO dto) {
        ClassTypeEntity entity = new ClassTypeEntity();
        entity.setId(dto.getId());
        entity.setStudioId(dto.getStudioId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setCapacity(dto.getCapacity());
        entity.setLevel(dto.getLevel());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
