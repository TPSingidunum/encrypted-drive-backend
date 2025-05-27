package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.configs.encryption.EncryptionProperties;
import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.security.JwtService;
import com.singidunum.encrypted_drive_backend.dtos.RefreshTokenDto;
import com.singidunum.encrypted_drive_backend.dtos.UserLoginDto;
import com.singidunum.encrypted_drive_backend.dtos.UserRegisterDto;
import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.services.StorageService;
import com.singidunum.encrypted_drive_backend.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final StorageService storageService;
    private final JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private EncryptionProperties encryptionProperties;

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

        if (userService.existsByUsername(data.getUsername())) {
            throw new CustomException("Username exists", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        if (userService.existsByEmail(data.getEmail())) {
            throw new CustomException("Email exists", HttpStatus.BAD_REQUEST, ErrorCode.USER_EMAIL_EXIST);
        }

        if (!data.getPassword().equals(data.getConfirmPassword())) {
            throw new CustomException("Passwords do not match", HttpStatus.BAD_REQUEST, ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        User newUser = userService.registerUser(data);

        if (newUser == null) {
            throw new CustomException("ERROR", HttpStatus.BAD_REQUEST, ErrorCode.BASE_ERROR);
        }

        storageService.createUserWorkspace(newUser);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenDto data) {
        Claims claims;

        try {
            claims = jwtService.extractAllClaimsFromToken(data.getRefreshToken());
        } catch (Exception e) {
            throw new CustomException("Jwt-Refresh Token Not Valid", HttpStatus.BAD_REQUEST, ErrorCode.BASE_ERROR);
        }

        if (!claims.get("type", String.class).equals("refresh")) {
            throw new CustomException("Not a Refresh token", HttpStatus.BAD_REQUEST, ErrorCode.BASE_ERROR);
        }

        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("email", claims.get("email", String.class));
        newClaims.put("role", claims.get("role", String.class));
        newClaims.put("type", "access");

        String accessToken = jwtService.generateToken(claims.getSubject(), newClaims);

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @GetMapping("/certificate")
    public ResponseEntity<?> downloadFile() {
        Path certificatePath = Path.of(encryptionProperties.getCertificatePath());
        try {
            Resource resource = new UrlResource(certificatePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException("Failed to get Certificate file", HttpStatus.BAD_REQUEST, ErrorCode.FAILED_TO_GET_CERTIFICATE);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.cer\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
