package com.kbseed.service;

import com.kbseed.dto.AuthRequest;
import com.kbseed.dto.AuthResponse;
import com.kbseed.dto.MeResponse;
import com.kbseed.entity.StudioEntity;
import com.kbseed.entity.UserEntity;
import com.kbseed.repository.StudioRepository;
import com.kbseed.repository.UserRepository;
import com.kbseed.support.SessionContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final SessionContext sessionContext;
    private final StudioRepository studioRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, HttpSession session, SessionContext sessionContext, StudioRepository studioRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
        this.sessionContext = sessionContext;
        this.studioRepository = studioRepository;
    }

    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
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
        Long userId = sessionContext.requireUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        StudioEntity studio = user.getStudioId() != null ? studioRepository.findById(user.getStudioId()).orElse(null) : null;

        MeResponse response = new MeResponse();
        response.setId(user.getId());
        response.setStudioId(user.getStudioId());
        response.setStudioName(studio != null ? studio.getName() : "Studio Admin");
        response.setUsername(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(resolveRole(user.getRoleId()));
        return response;
    }

    public void logout() {
        session.invalidate();
    }

    private String resolveRole(Long roleId) {
        if (roleId == null) {
            return "ADMIN";
        }
        return switch (roleId.intValue()) {
            case 1 -> "ADMIN";
            case 2 -> "COACH";
            case 3 -> "RECEPCION";
            default -> roleId.toString();
        };
    }
}
