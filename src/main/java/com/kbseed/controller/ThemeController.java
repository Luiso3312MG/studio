package com.kbseed.controller;

import com.kbseed.dto.ThemeDTO;
import com.kbseed.service.ThemeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {
    private final ThemeService themeService;
    private final HttpSession session;

    public ThemeController(ThemeService themeService, HttpSession session) {
        this.themeService = themeService;
        this.session = session;
    }

    @GetMapping("/current")
    public ThemeDTO obtenerTemaActual() {
        Long studioId = (Long) session.getAttribute("STUDIO_ID");
        if (studioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return themeService.obtenerPorStudioId(studioId);
    }
}
