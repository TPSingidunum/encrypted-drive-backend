package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.dtos.PublicKeyDto;
import com.singidunum.encrypted_drive_backend.dtos.UserDto;
import com.singidunum.encrypted_drive_backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserInformation() {
        UserDto userDto = userService.getUserInformation();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        String publicKey = userService.getPublicKey();
        boolean result = !publicKey.isEmpty();
        return ResponseEntity.ok().body(Map.of("success", result));
    }

    @PostMapping("/register/public-key")
    public ResponseEntity<?> registerPublicKey(@Valid @RequestBody PublicKeyDto data) {
        userService.setPublicKey(data);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

}
