package com.kbseed.service;

import com.kbseed.dto.AuthRequest;
import com.kbseed.dto.AuthResponse;
import com.kbseed.dto.MeResponse;
import com.kbseed.entity.UserEntity;
import com.kbseed.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final HttpSession session;

    public AuthService(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    public AuthResponse login(AuthRequest request) {
        System.out.println(">>> Intentando login con username: " + request.getUsername());

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en BD"));

        System.out.println(">>> Usuario encontrado: " + user.getUsername());
        System.out.println(">>> StudioId: " + user.getStudioId());
        System.out.println(">>> Role: " + user.getRole());
        System.out.println(">>> Status: " + user.getStatus());

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (user.getStatus() != null && !user.getStatus().equalsIgnoreCase("ACTIVO")) {
            throw new RuntimeException("Usuario inactivo");
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("STUDIO_ID", user.getStudioId());
        session.setAttribute("USERNAME", user.getUsername());
        session.setAttribute("ROLE", user.getRole());

        AuthResponse response = new AuthResponse();
        response.setAuthenticated(true);
        response.setUserId(user.getId());
        response.setStudioId(user.getStudioId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());

        return response;
    }

    public MeResponse me() {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new RuntimeException("No autenticado");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MeResponse response = new MeResponse();
        response.setId(user.getId());
        response.setStudioId(user.getStudioId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());

        return response;
    }

    public void logout() {
        session.invalidate();
    }
}
