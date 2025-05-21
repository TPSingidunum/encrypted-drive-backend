package com.singidunum.encrypted_drive_backend.configs.storage;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@AllArgsConstructor
public class StorageInitializer implements ApplicationRunner {

    private final StorageProperties properties;

    @Override
    public void run(ApplicationArguments arg) {
        try {
            Path basePath = getCurrentAppPath();
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getCurrentAppPath() {
       return Paths.get(System.getProperty("user.dir"), properties.getLocalStorageLocation()).toAbsolutePath().normalize();
    }
}
