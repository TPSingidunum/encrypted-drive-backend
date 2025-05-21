package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.services.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@AllArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        boolean result = storageService.storeFile(file);
        return ResponseEntity.ok(Map.of("success", result));
    }
}
