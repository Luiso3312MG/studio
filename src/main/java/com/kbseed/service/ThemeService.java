package com.kbseed.service;

import com.kbseed.dto.ThemeDTO;
import com.kbseed.entity.ThemeEntity;
import com.kbseed.repository.ThemeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeDTO> obtenerTodos() {
        return themeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ThemeDTO obtenerPorStudioId(Long studioId) {
    ThemeEntity entity = themeRepository.findByStudioId(studioId)
            .orElse(null);

    return entity != null ? toDTO(entity) : null;
}


    private ThemeDTO toDTO(ThemeEntity entity) {
        ThemeDTO dto = new ThemeDTO();
        dto.setId(entity.getId());
        dto.setStudioId(entity.getStudioId());
        dto.setPrimaryColor(entity.getPrimaryColor());
        dto.setSecondaryColor(entity.getSecondaryColor());
        dto.setBackgroundColor(entity.getBackgroundColor());
        return dto;
    }
}
