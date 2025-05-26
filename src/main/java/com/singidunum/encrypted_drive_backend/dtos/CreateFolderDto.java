package com.singidunum.encrypted_drive_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFolderDto {
    @NotBlank(message = "Must enter folder name")
    String name;
    int parentId;
    int workspaceId;
}
