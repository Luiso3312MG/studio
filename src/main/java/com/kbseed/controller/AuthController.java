package com.kbseed.controller;

import com.kbseed.dto.AuthRequest;
import com.kbseed.dto.AuthResponse;
import com.kbseed.dto.MeResponse;
import com.kbseed.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        return authService.me();
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
