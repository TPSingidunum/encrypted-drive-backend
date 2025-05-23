package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.entities.Workspace;
import com.singidunum.encrypted_drive_backend.services.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@AllArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @GetMapping("/workspaces")
    public ResponseEntity<?> getAllUserWorkspaces(){
        List<Workspace> workspaces = storageService.getAllUserWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> getAllWorkspaceChildren(@PathVariable("workspaceId") int workspaceId){
        Map<String, Object> children = storageService.getAllChildrenByWorkspaceId(workspaceId);
        return ResponseEntity.ok(children);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("File upload");
        boolean result = storageService.storeFile(file);
        return ResponseEntity.ok(Map.of("success", result));
    }
}
