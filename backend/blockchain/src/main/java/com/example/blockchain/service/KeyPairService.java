package com.example.blockchain.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;

@Getter
@Service
public class KeyPairService {

    private static final String PRIVATE_KEY_FILE = "private.key";
    private static final String PUBLIC_KEY_FILE = "public.key";

    private KeyPair keyPair;

    @PostConstruct
    public void init() throws Exception {
        File privateKeyFile = new File(PRIVATE_KEY_FILE);
        File publicKeyFile = new File(PUBLIC_KEY_FILE);

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            keyPair = loadKeyPair(privateKeyFile, publicKeyFile);
        } else {
            keyPair = generateKeyPair();
            saveKeyPair(keyPair, privateKeyFile, publicKeyFile);
        }
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private void saveKeyPair(KeyPair keyPair, File privateKeyFile, File publicKeyFile) throws IOException {
        try (FileOutputStream privOut = new FileOutputStream(privateKeyFile);
             FileOutputStream pubOut = new FileOutputStream(publicKeyFile)) {
            privOut.write(keyPair.getPrivate().getEncoded());
            pubOut.write(keyPair.getPublic().getEncoded());
        }
    }

    private KeyPair loadKeyPair(File privateKeyFile, File publicKeyFile) throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        return new KeyPair(publicKey, privateKey);
    }
}
