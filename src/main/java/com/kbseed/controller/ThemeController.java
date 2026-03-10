package com.kbseed.controller;

import com.kbseed.dto.ThemeDTO;
import com.kbseed.service.ThemeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<ThemeDTO> obtenerTodos() {
        return themeService.obtenerTodos();
    }

    @GetMapping("/{studioId}")
    public ThemeDTO obtenerPorStudioId(@PathVariable Long studioId) {
        return themeService.obtenerPorStudioId(studioId);
    }
}
