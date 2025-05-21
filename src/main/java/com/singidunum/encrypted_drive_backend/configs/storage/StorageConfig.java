package com.singidunum.encrypted_drive_backend.configs.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@Component
public class StorageConfig {
    private Path basePath;

    public Path getStoragePath(String path) {
        return Paths.get(basePath.toString(), path).normalize();
    }
}
