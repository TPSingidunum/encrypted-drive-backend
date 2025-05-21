package com.singidunum.encrypted_drive_backend.services;

import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.storage.StorageConfig;
import com.singidunum.encrypted_drive_backend.entities.File;
import com.singidunum.encrypted_drive_backend.entities.Folder;
import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.entities.Workspace;
import com.singidunum.encrypted_drive_backend.repositories.FileRepository;
import com.singidunum.encrypted_drive_backend.repositories.FolderRepository;
import com.singidunum.encrypted_drive_backend.repositories.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class StorageService {
    private final FileRepository fileRepository;
    private final WorkspaceRepository workspaceRepository;
    private final FolderRepository folderRepository;
    private StorageConfig storageConfig;

    public void createUserWorkspace(User user) {
        Path storagePath = storageConfig.getStoragePath(String.valueOf(user.getUserId()));
        try {
            Files.createDirectories(storagePath);

            Workspace workspace = new Workspace();
            workspace.setName(user.getUsername() + " - Workspace");
            workspace.setUserId(user.getUserId());
            workspaceRepository.save(workspace);

        } catch (IOException e) {
            throw new CustomException("Failed to crate default user workspace", HttpStatus.BAD_REQUEST, ErrorCode.FAILED_WORKSPACE_CREATION);
        }
    }
}
