package com.civicshield.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AuthController {
    @GetMapping("/api/auth/me")
    public Map<String, Object> me(Authentication authentication) {
        boolean admin = authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return Map.of("authenticated", admin, "admin", admin, "username", admin ? authentication.getName() : "");
    }

    @GetMapping("/api/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }
}
