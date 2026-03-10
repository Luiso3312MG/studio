package com.kbseed.service;

import com.kbseed.dto.AuthRequest;
import com.kbseed.dto.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthResponse login(AuthRequest request) {
        // Aquí iría la lógica real de autenticación
        AuthResponse response = new AuthResponse();
        response.setToken("dummy-token");
        return response;
    }
}
