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
}
