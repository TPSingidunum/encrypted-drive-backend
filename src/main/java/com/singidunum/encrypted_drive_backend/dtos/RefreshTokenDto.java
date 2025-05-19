package com.singidunum.encrypted_drive_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDto {

   @NotBlank(message = "Username is required")
   private String refreshToken;
}
