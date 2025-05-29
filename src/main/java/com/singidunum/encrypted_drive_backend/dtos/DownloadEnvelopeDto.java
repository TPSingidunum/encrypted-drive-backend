package com.singidunum.encrypted_drive_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class DownloadEnvelopeDto {
    private Resource resource;
    private String key;
    private String iv;
    private String filename;
}