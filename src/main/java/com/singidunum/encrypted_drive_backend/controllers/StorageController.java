package com.singidunum.encrypted_drive_backend.controllers;

import com.singidunum.encrypted_drive_backend.dtos.CreateFolderDto;
import com.singidunum.encrypted_drive_backend.entities.Workspace;
import com.singidunum.encrypted_drive_backend.services.StorageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/workspace/{workspaceId}/children")
    public ResponseEntity<?> getAllWorkspaceChildren(@PathVariable("workspaceId") int workspaceId){
        Map<String, Object> children = storageService.getAllChildrenByWorkspaceId(workspaceId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/folder/{folderId}/children")
    public ResponseEntity<?> getAllFolderChildren(@PathVariable("folderId") int folderId){
        Map<String, Object> children = storageService.getAllChildrenByFolderId(folderId);
        return ResponseEntity.ok(children);
    }

    @PostMapping("/folder")
    public ResponseEntity<?> createFolder(@Valid @RequestBody CreateFolderDto data){
        storageService.createFolder(data);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("workspaceId") int workspaceId, @RequestParam("folderId") int folderId) {
        boolean result = storageService.storeFile(workspaceId, folderId, file);
        return ResponseEntity.ok(Map.of("success", result));
    }

    @GetMapping("/download/file/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") int fileId) {
        Resource resource = storageService.loadFile(fileId);
        String filename = resource.getFilename();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename +"\"")
                .body(resource);
    }
}
