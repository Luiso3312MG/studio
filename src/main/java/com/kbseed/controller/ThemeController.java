package com.kbseed.controller;

import com.kbseed.dto.ThemeDTO;
import com.kbseed.service.ThemeService;
import com.kbseed.support.SessionContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {
    private final ThemeService themeService;
    private final SessionContext sessionContext;

    public ThemeController(ThemeService themeService, SessionContext sessionContext) {
        this.themeService = themeService;
        this.sessionContext = sessionContext;
    }

    @GetMapping("/current")
    public ThemeDTO obtenerTemaActual() {
        Long studioId = sessionContext.requireStudioId();
        return themeService.obtenerPorStudioId(studioId);
    }
}
