package com.kbseed.service;

import com.kbseed.dto.RoomDTO;
import com.kbseed.entity.RoomEntity;
import com.kbseed.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDTO> obtenerTodos() {
        return roomRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> obtenerPorStudioId(Long studioId) {
        return roomRepository.findByStudioId(studioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO obtenerPorId(Long id) {
        RoomEntity entity = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + id));
        return toDTO(entity);
    }

    public RoomDTO crear(RoomDTO dto) {
        RoomEntity entity = toEntity(dto);
        entity.setId(null);

        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }

        RoomEntity saved = roomRepository.save(entity);
        return toDTO(saved);
    }

    public RoomDTO actualizar(Long id, RoomDTO dto) {
        RoomEntity entity = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + id));

        entity.setStudioId(dto.getStudioId());
        entity.setName(dto.getName());
        entity.setCapacity(dto.getCapacity());
        entity.setLocationNote(dto.getLocationNote());
        entity.setIsActive(dto.getIsActive());

        RoomEntity updated = roomRepository.save(entity);
        return toDTO(updated);
    }

    public void eliminar(Long id) {
        RoomEntity entity = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + id));

        roomRepository.delete(entity);
    }

    private RoomDTO toDTO(RoomEntity entity) {
        RoomDTO dto = new RoomDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setName(entity.getName());
        dto.setCapacity(entity.getCapacity());
        dto.setLocationNote(entity.getLocationNote());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    private RoomEntity toEntity(RoomDTO dto) {
        RoomEntity entity = new RoomEntity();
        entity.setId(dto.getId());
        entity.setStudioId(dto.getStudioId());
        entity.setName(dto.getName());
        entity.setCapacity(dto.getCapacity());
        entity.setLocationNote(dto.getLocationNote());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
