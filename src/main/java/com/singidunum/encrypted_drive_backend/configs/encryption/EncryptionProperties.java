package com.singidunum.encrypted_drive_backend.configs.encryption;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "encryption")
public class EncryptionProperties {
    private String certificatePath;
    private String privateKeyPath;
}
