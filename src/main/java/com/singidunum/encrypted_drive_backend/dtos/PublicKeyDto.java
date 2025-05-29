package com.singidunum.encrypted_drive_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublicKeyDto {
    @NotBlank(message = "PublicKey is required")
    private String publicKey;
}