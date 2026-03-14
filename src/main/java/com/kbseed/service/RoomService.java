package com.kbseed.service;

import com.kbseed.dto.RoomDTO;
import com.kbseed.entity.RoomEntity;
import com.kbseed.repository.RoomRepository;
import com.kbseed.support.SessionContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final SessionContext sessionContext;

    public RoomService(RoomRepository roomRepository, SessionContext sessionContext) {
        this.roomRepository = roomRepository;
        this.sessionContext = sessionContext;
    }

    public List<RoomDTO> obtenerTodos() {
        Long studioId = sessionContext.requireStudioId();
        return roomRepository.findByStudioId(studioId).stream().map(this::toDTO).toList();
    }

    public RoomDTO obtenerPorId(Long id) {
        Long studioId = sessionContext.requireStudioId();
        RoomEntity entity = roomRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + id));
        return toDTO(entity);
    }

    public RoomDTO crear(RoomDTO dto) {
        RoomEntity entity = toEntity(dto);
        entity.setId(null);
        entity.setStudioId(sessionContext.requireStudioId());
        if (entity.getIsActive() == null) entity.setIsActive(true);
        if (entity.getRoomType() == null || entity.getRoomType().isBlank()) entity.setRoomType("SALON");
        return toDTO(roomRepository.save(entity));
    }

    public RoomDTO actualizar(Long id, RoomDTO dto) {
        Long studioId = sessionContext.requireStudioId();
        RoomEntity entity = roomRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
                .orElseThrow(() -> new RuntimeException("Salón no encontrado con id: " + id));
        entity.setName(dto.getName());
        entity.setCapacity(dto.getCapacity());
        entity.setLocationNote(dto.getLocationNote());
        entity.setRoomType(dto.getRoomType() == null || dto.getRoomType().isBlank() ? "SALON" : dto.getRoomType());
        entity.setIsActive(dto.getIsActive());
        return toDTO(roomRepository.save(entity));
    }

    public void eliminar(Long id) {
        Long studioId = sessionContext.requireStudioId();
        RoomEntity entity = roomRepository.findById(id)
                .filter(item -> item.getStudioId().equals(studioId))
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
        dto.setRoomType(entity.getRoomType());
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
        entity.setRoomType(dto.getRoomType());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
}
