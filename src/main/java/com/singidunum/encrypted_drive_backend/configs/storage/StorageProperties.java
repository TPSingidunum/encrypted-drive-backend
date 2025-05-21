package com.singidunum.encrypted_drive_backend.configs.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String localStorageLocation;
}
