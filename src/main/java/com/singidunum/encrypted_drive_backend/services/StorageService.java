package com.singidunum.encrypted_drive_backend.services;

import com.singidunum.encrypted_drive_backend.configs.encryption.EncryptionProperties;
import com.singidunum.encrypted_drive_backend.configs.encryption.EncryptionUtility;
import com.singidunum.encrypted_drive_backend.configs.exceptions.CustomException;
import com.singidunum.encrypted_drive_backend.configs.exceptions.ErrorCode;
import com.singidunum.encrypted_drive_backend.configs.mapper.MapperConfig;
import com.singidunum.encrypted_drive_backend.configs.security.JwtClaims;
import com.singidunum.encrypted_drive_backend.configs.storage.StorageConfig;
import com.singidunum.encrypted_drive_backend.dtos.*;
import com.singidunum.encrypted_drive_backend.entities.*;
import com.singidunum.encrypted_drive_backend.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@AllArgsConstructor
public class StorageService {
    private final FileRepository fileRepository;
    private final WorkspaceRepository workspaceRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final EnvelopeRepository envelopeRepository;
    private final JwtClaims jwtClaims;
    private final StorageConfig storageConfig;
    private final EncryptionUtility encryptionUtility;
    private final EncryptionProperties encryptionProperties;
    private final MapperConfig mapperConfig;

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

    public boolean storeFile(int workspaceId, int folderId, MultipartFile file) throws IllegalBlockSizeException, BadPaddingException, IOException {
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
        String username = jwtClaims.getUsername();
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new CustomException("Failed to get User", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        SecretKey key = encryptionUtility.generateSecretKey();
        byte[] IV = encryptionUtility.generateIv();
        Cipher cipher = encryptionUtility.initializeCipher(Cipher.ENCRYPT_MODE, key, IV);

        Path encryptedTarget = storageConfig.getStoragePath(user.get().getUserId() + "/" + UUID.randomUUID());
        try {
            Files.createDirectories(encryptedTarget.getParent());
            try (
                    var in = file.getInputStream();
                    OutputStream out = Files.newOutputStream(encryptedTarget);
                    CipherOutputStream cos = new CipherOutputStream(out, cipher)
            ) {
                byte[] buffer = new byte[encryptionProperties.getChunkSize()];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new CustomException("Failed to store file", HttpStatus.BAD_REQUEST, ErrorCode.FAILED_FILE_STORE);
        }

        File newFile = new File();
        newFile.setWorkspaceId(workspaceId);
        newFile.setName(filename);
        newFile.setPath(encryptedTarget.toString());
        if (folderId != 0) {
            newFile.setParentId(folderId);
        }
        File savedFile = fileRepository.save(newFile);

        Envelope envelope = new Envelope();
        String envelopeKey = encryptionUtility.createEnvelopeKey(key, user.get().getPublicKey());
        envelope.setFileId(savedFile.getFileId());
        envelope.setEncryptionKey(envelopeKey);
        envelope.setUserId(user.get().getUserId());
        envelope.setIv(Base64.getEncoder().encodeToString(IV));
        envelopeRepository.save(envelope);

        return true;
    }

    public List<WorkspaceDto> getAllUserWorkspaces() {
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if(user.isEmpty()) {
            throw new CustomException("Failed to get User", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }
        List<WorkspaceDto> workspaceDtos = new ArrayList<>();
        workspaceRepository.findAllByUserId(user.get().getUserId()).forEach(
                workspace -> workspaceDtos.add(mapperConfig.modelMapper().map(workspace, WorkspaceDto.class))
        );
        return workspaceDtos;
    }

    public Map<String, Object> getAllChildrenByWorkspaceId(int workspaceId) {
        List<FileDto> files = new ArrayList<>();
        List<FolderDto> folders = new ArrayList<>();
        fileRepository.findAllByParentIdIsNullAndWorkspaceId(workspaceId).forEach(file -> files.add(mapperConfig.modelMapper().map(file, FileDto.class)));
        folderRepository.findAllByParentIdIsNullAndWorkspaceId(workspaceId).forEach(folder -> folders.add(mapperConfig.modelMapper().map(folder, FolderDto.class)));

        return Map.of("files", files, "folders", folders);
    }

    public Map<String, Object> getAllChildrenByFolderId(int folderId) {
        List<FileDto> files = new ArrayList<>();
        List<FolderDto> folders = new ArrayList<>();
        fileRepository.findAllByParentId(folderId).forEach(file -> files.add(mapperConfig.modelMapper().map(file, FileDto.class)));
        folderRepository.findAllByParentId(folderId).forEach(folder -> folders.add(mapperConfig.modelMapper().map(folder, FolderDto.class)));

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

            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException("Failed to load file", HttpStatus.BAD_REQUEST, ErrorCode.STORAGE_FILE_DOES_NOT_EXIST);
        }
    }

    public DownloadEnvelopeDto loadFileWithEnvelope(int fileId) {
        Resource resource = loadFile(fileId);
        Optional<User> user = userRepository.findByUsername(jwtClaims.getUsername());

        if (user.isEmpty()) {
            throw new CustomException("Failed to get User", HttpStatus.BAD_REQUEST, ErrorCode.USER_USERNAME_EXIST);
        }

        Envelope envelope = envelopeRepository.findByFileIdAndUserId(fileId, user.get().getUserId());
        if (envelope == null) {
            throw new CustomException("Failed to get Envelope", HttpStatus.BAD_REQUEST, ErrorCode.BASE_ERROR);
        }

        Optional<File> file = fileRepository.findById(fileId);
        String filename = file.map(File::getName).orElse("file");

        return new DownloadEnvelopeDto(resource, envelope.getEncryptionKey(), envelope.getIv(), filename);
    }
}
