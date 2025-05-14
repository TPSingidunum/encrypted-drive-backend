package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.security.JwtService;
import com.singidunum.encrypted_drive_backend.dtos.UserLoginDto;
import com.singidunum.encrypted_drive_backend.dtos.UserRegisterDto;
import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto data) {

        Optional<User> user = userService.getUserByUsername(data.getUsername());
        if (user.isEmpty()) {
            throw new CustomException(
                    "User with that username does not exist",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.USER_NOT_EXIST
            );
        }

        if (!passwordEncoder.matches(data.getPassword(), user.get().getPassword())) {
            throw new CustomException(
                    "Passwords do not match",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.PASSWORDS_DO_NOT_MATCH
            );
        }

        // Kreni proceduru pravljenja tokena
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.get().getEmail());
        claims.put("role", "admin");
        claims.put("type", "access");

        String accessToken = jwtService.generateToken(user.get().getUsername(), claims);
        claims.put("type", "refresh");
        String refreshToken = jwtService.generateToken(user.get().getUsername(), claims);

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto data) {
        // Da li postoji korinsik sa tim username-om i email-om
        if (userService.existsByUsername(data.getUsername())) {
            throw new CustomException("Username exists", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        if (userService.existsByEmail(data.getUsername())) {
            throw new CustomException("Email exists", HttpStatus.BAD_REQUEST, ErrorCode.USER_EMAIL_EXIST);
        }

        if (!data.getPassword().equals(data.getConfirmPassword())) {
            throw new CustomException("Passwords do not match", HttpStatus.BAD_REQUEST, ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        User newUser = userService.registerUser(data);

        return ResponseEntity.ok(newUser);
    }
}
