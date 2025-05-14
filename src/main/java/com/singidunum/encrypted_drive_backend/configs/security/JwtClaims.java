package com.singidunum.encrypted_drive_backend.configs.security;

import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtClaims {
    public Claims getClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Claims claims)) {
            throw new CustomException("Jwt Claims not in Context", HttpStatus.BAD_REQUEST, ErrorCode.JWT_CLAIMS_MISSING);
        }

        return claims;
    }

    public <T> T get(String name, Class<T> type) {
        return getClaims().get(name, type);
    }

    public String getSubject() {
        return getClaims().getSubject();
    }

    public String getUsername() {
        return getClaims().get("username", String.class);
    }
}
