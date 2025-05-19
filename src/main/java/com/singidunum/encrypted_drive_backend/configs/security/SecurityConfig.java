package com.singidunum.encrypted_drive_backend.configs.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(List.of("http://localhost:5174"));
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            //TODO Tip Header-a mozda pravi problem
                            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                            return configuration;
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").hasAnyRole("admin", "user")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
