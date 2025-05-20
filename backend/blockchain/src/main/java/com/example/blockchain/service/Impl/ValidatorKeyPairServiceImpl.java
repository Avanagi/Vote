package com.example.blockchain.service.Impl;

import com.example.blockchain.service.ValidatorKeyPairService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Getter
@Setter
@Service
public class ValidatorKeyPairServiceImpl implements ValidatorKeyPairService {

    private static final String PRIVATE_KEY_FILE = "private.key";
    private static final String PUBLIC_KEY_FILE = "public.key";

    private KeyPair keyPair;

    @PostConstruct
    public void init() {
        File privateKeyFile = new File(PRIVATE_KEY_FILE);
        File publicKeyFile = new File(PUBLIC_KEY_FILE);

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            keyPair = loadKeyPair(privateKeyFile, publicKeyFile);
        } else {
            keyPair = generateKeyPair();
            saveKeyPair(keyPair, privateKeyFile, publicKeyFile);
        }
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException(noSuchAlgorithmException);
        }
    }

    public void saveKeyPair(KeyPair keyPair, File privateKeyFile, File publicKeyFile) {
        try (FileOutputStream privateOut = new FileOutputStream(privateKeyFile);
             FileOutputStream publicOut = new FileOutputStream(publicKeyFile)) {
            privateOut.write(keyPair.getPrivate().getEncoded());
            publicOut.write(keyPair.getPublic().getEncoded());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public KeyPair loadKeyPair(File privateKeyFile, File publicKeyFile) {
        try {
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            return new KeyPair(publicKey, privateKey);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException securityException) {
            throw new RuntimeException(securityException);
        }
    }
}
