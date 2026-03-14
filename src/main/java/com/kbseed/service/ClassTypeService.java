package com.kbseed.service;

import com.kbseed.dto.ClassTypeDTO;
import com.kbseed.entity.ClassTypeEntity;
import com.kbseed.repository.ClassTypeRepository;
import com.kbseed.support.SessionContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTypeService {
    private final ClassTypeRepository classTypeRepository;
    private final SessionContext sessionContext;

    public ClassTypeService(ClassTypeRepository classTypeRepository, SessionContext sessionContext) {
        this.classTypeRepository = classTypeRepository;
        this.sessionContext = sessionContext;
    }

    public List<ClassTypeDTO> obtenerTodos() {
        Long studioId = sessionContext.requireStudioId();
        return classTypeRepository.findByStudioId(studioId).stream().map(this::toDTO).toList();
    }

    public List<ClassTypeDTO> obtenerDisciplinas() {
        Long studioId = sessionContext.requireStudioId();
        return classTypeRepository.findByStudioIdAndCategoryAndIsActiveTrue(studioId, "DISCIPLINA").stream().map(this::toDTO).toList();
    }

    public ClassTypeDTO obtenerPorId(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado con id: " + id));
        return toDTO(entity);
    }

    public ClassTypeDTO crear(ClassTypeDTO dto) {
        ClassTypeEntity entity = toEntity(dto);
        entity.setId(null);
        entity.setStudioId(sessionContext.requireStudioId());
        if (entity.getIsActive() == null) entity.setIsActive(true);
        if (entity.getLevel() == null || entity.getLevel().isBlank()) entity.setLevel("ALL");
        if (entity.getCategory() == null || entity.getCategory().isBlank()) entity.setCategory("DISCIPLINA");
        if (entity.getServiceKind() == null || entity.getServiceKind().isBlank()) entity.setServiceKind("NONE");
        return toDTO(classTypeRepository.save(entity));
    }

    public ClassTypeDTO actualizar(Long id, ClassTypeDTO dto) {
        Long studioId = sessionContext.requireStudioId();
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Tipo de clase no encontrado con id: " + id));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setCapacity(dto.getCapacity());
        entity.setLevel(dto.getLevel());
        entity.setCategory(dto.getCategory() == null || dto.getCategory().isBlank() ? "DISCIPLINA" : dto.getCategory());
        entity.setServiceKind(dto.getServiceKind() == null || dto.getServiceKind().isBlank() ? "NONE" : dto.getServiceKind());
        entity.setIsActive(dto.getIsActive());
        return toDTO(classTypeRepository.save(entity));
    }

    public void eliminar(Long id) {
        Long studioId = sessionContext.requireStudioId();
        ClassTypeEntity entity = classTypeRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
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
        dto.setCategory(entity.getCategory());
        dto.setServiceKind(entity.getServiceKind());
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
        entity.setCategory(dto.getCategory());
        entity.setServiceKind(dto.getServiceKind());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
