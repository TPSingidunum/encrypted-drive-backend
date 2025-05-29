package com.singidunum.encrypted_drive_backend.dtos;

import lombok.Data;

@Data
public class UserDto {
    private Integer userId;
    private String username;
    private String email;
    private String publicKey;
}