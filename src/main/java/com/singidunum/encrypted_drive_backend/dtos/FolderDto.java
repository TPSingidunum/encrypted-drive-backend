package com.singidunum.encrypted_drive_backend.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FolderDto {
    private Integer folderId;
    private String name;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
