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
    private String issuerCommonName = "Singidunum";
    private String issuerOrganization = "Singidunum";
    private String issuerCountry = "Singidunum";
    private String algorithm;
    private int rsaKeySize = 2048;
    private int aesKeySize = 256;
    private int gcmTagSize = 128;
    private int ivSize = 96;
    private int chunkSize;
}
