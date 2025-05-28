package com.singidunum.encrypted_drive_backend.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WorkspaceDto {
    private int workspaceId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
