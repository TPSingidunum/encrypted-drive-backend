package com.singidunum.encrypted_drive_backend.services;

import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.security.JwtClaims;
import com.singidunum.encrypted_drive_backend.configs.storage.StorageConfig;
import com.singidunum.encrypted_drive_backend.dtos.CreateFolderDto;
import com.singidunum.encrypted_drive_backend.entities.File;
import com.singidunum.encrypted_drive_backend.entities.Folder;
import com.singidunum.encrypted_drive_backend.entities.User;
import com.singidunum.encrypted_drive_backend.entities.Workspace;
import com.singidunum.encrypted_drive_backend.repositories.FileRepository;
import com.singidunum.encrypted_drive_backend.repositories.FolderRepository;
import com.singidunum.encrypted_drive_backend.repositories.UserRepository;
import com.singidunum.encrypted_drive_backend.repositories.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageService {
    private final FileRepository fileRepository;
    private final WorkspaceRepository workspaceRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final JwtClaims jwtClaims;
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

    public void createFolder(CreateFolderDto data) {
        Folder newFolder = new Folder();
        newFolder.setName(data.getName());
        newFolder.setWorkspaceId(data.getWorkspaceId());

        if (data.getParentId() != 0) {
            newFolder.setParentId(data.getParentId());
        }

        folderRepository.save(newFolder);
    }

    public boolean storeFile(int workspaceId, int folderId, MultipartFile file) {
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        String username = jwtClaims.getUsername();
        System.out.println(username);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new CustomException("Failed to get User", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        try (var in = file.getInputStream()) {
            Path target = storageConfig.getStoragePath(String.valueOf(user.get().getUserId() + "\\" + filename));
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

            File newFile = new File();
            //TODO: User can have multiple workspaces
            newFile.setWorkspaceId(workspaceId);
            newFile.setName(filename);
            newFile.setPath(target.toString());
            if (folderId != 0) {
                newFile.setParentId(folderId);
            }
            fileRepository.save(newFile);

            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Workspace> getAllUserWorkspaces() {
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if(user.isEmpty()) {
            throw new CustomException("Failed to get User", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        return workspaceRepository.findAllByUserId(user.get().getUserId());
    }

    public Map<String, Object> getAllChildrenByWorkspaceId(int workspaceId) {
        List<File> files = fileRepository.findAllByParentIdIsNullAndWorkspaceId(workspaceId);
        List<Folder> folders = folderRepository.findAllByParentIdIsNullAndWorkspaceId(workspaceId);

        return Map.of("files", files, "folders", folders);
    }

    public Map<String, Object> getAllChildrenByFolderId(int folderId) {
        List<File> files = fileRepository.findAllByParentId(folderId);
        List<Folder> folders = folderRepository.findAllByParentId(folderId);

        return Map.of("files", files, "folders", folders);
    }

    public Resource loadFile(int fileId) {
        Optional<File> file = fileRepository.findById(fileId);

        if (file.isEmpty()) {
            throw new CustomException("Failed to get File", HttpStatus.BAD_REQUEST, ErrorCode.FILE_DOES_NOT_EXIST);
        }

        Path path = Path.of(file.get().getPath());

        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException("Failed to get Storage file", HttpStatus.BAD_REQUEST, ErrorCode.STORAGE_FILE_DOES_NOT_EXIST);
            }

            return  resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
