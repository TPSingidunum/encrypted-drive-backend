package com.singidunum.encrypted_drive_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {

   @NotBlank(message = "Username is required")
   @Size(min = 3, max = 60, message = "Username has to be between 3 and 60 chars")
   private String username;

   @NotBlank(message = "Email is required")
   @Email(message = "Must be a valid email")
   @Pattern(regexp = ".*@singimail\\.rs$", message = "Must be @singimail.rs")
   private String email;

   @NotBlank(message = "Password is required")
   private String password;

   @NotBlank(message = "ConfirmPassword is required")
   private String confirmPassword;
}
