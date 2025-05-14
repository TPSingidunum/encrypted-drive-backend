package com.singidunum.encrypted_drive_backend.configs.security;

import com.singidunum.encrypted_drive_backend.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Claims claims;

        try {
            claims = jwtService.extractAllClaimsFromToken(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{ 'error' : '" + e.getMessage() + "'}");
            response.setContentType("application/json");
            return;
        }

        if (!userService.existsByUsername(claims.getSubject())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Definisanje privilegija (U okviru baze users -> roles, permissions -> object )
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = claims.get("role", String.class);
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (role != null && !role.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims,
                    null,
                    authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
