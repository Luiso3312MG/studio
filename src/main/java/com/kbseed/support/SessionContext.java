package com.kbseed.support;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SessionContext {

    private final HttpSession session;

    public SessionContext(HttpSession session) {
        this.session = session;
    }

    public Long requireUserId() {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return userId;
    }

    public Long requireStudioId() {
        Long studioId = (Long) session.getAttribute("STUDIO_ID");
        if (studioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return studioId;
    }

    public String requireEmail() {
        String email = (String) session.getAttribute("EMAIL");
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return email;
    }
}
