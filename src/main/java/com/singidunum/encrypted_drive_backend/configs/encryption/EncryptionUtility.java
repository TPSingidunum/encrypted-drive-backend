package com.singidunum.encrypted_drive_backend.configs.encryption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Setter
@AllArgsConstructor
@Component
public class EncryptionUtility {
    private final EncryptionProperties encryptionProperties;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(encryptionProperties.getAesKeySize());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateIv() {
        byte[] IV = new byte[encryptionProperties.getIvSize() / 8];
        // TODO: namestiti da ima SEED securerandom, (da li ima smisla u ovoj situaciji)
        new SecureRandom().nextBytes(IV);
        return IV;
    }

    public Cipher initializeCipher(int cipherMode, SecretKey key, byte[] IV) {
        try {
            Cipher cipher = Cipher.getInstance(encryptionProperties.getAlgorithm());
            GCMParameterSpec gcmSpec = new GCMParameterSpec(encryptionProperties.getGcmTagSize(), IV);
            cipher.init(cipherMode, key, gcmSpec);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey readPublicKeyFromPem(String pemKey) {
        try {
            String result = pemKey.replaceAll("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replaceAll("-----END RSA PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(result);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPublicKeyValid(String pemKey) {
        try{
            readPublicKeyFromPem(pemKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String createEnvelopeKey(SecretKey key, PublicKey userPubKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, userPubKey);
            byte[] encrypted = cipher.doFinal(key.getEncoded());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
