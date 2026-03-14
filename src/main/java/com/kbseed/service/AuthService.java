package com.kbseed.service;

import com.kbseed.dto.AuthRequest;
import com.kbseed.dto.AuthResponse;
import com.kbseed.dto.MeResponse;
import com.kbseed.entity.UserEntity;
import com.kbseed.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final HttpSession session;

    public AuthService(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    public AuthResponse login(AuthRequest request) {

        UserEntity user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos")
                );

        // Comparación simple temporal
        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("STUDIO_ID", user.getStudioId());
        session.setAttribute("EMAIL", user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setAuthenticated(true);
        response.setUserId(user.getId());
        response.setStudioId(user.getStudioId());
        response.setUsername(user.getEmail());
        response.setFullName(user.getFullName());

        return response;
    }

    public MeResponse me() {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
                );

        MeResponse response = new MeResponse();
        response.setId(user.getId());
        response.setStudioId(user.getStudioId());
        response.setUsername(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRoleId().toString());

        return response;
    }

    public void logout() {
        session.invalidate();
    }
}
