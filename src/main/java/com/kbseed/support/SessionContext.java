package com.kbseed.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SessionContext {

    private final HttpServletRequest request;

    public SessionContext(HttpServletRequest request) {
        this.request = request;
    }

    private HttpSession session() {
        return request.getSession(false);
    }

    public Long requireUserId() {
        HttpSession s = session();
        Long userId = s != null ? (Long) s.getAttribute("USER_ID") : null;
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return userId;
    }

    public Long requireStudioId() {
        HttpSession s = session();
        Long studioId = s != null ? (Long) s.getAttribute("STUDIO_ID") : null;
        if (studioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return studioId;
    }

    public String requireEmail() {
        HttpSession s = session();
        String email = s != null ? (String) s.getAttribute("EMAIL") : null;
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return email;
    }
}
