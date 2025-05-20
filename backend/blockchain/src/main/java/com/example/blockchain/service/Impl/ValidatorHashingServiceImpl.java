package com.example.blockchain.service.Impl;

import com.example.blockchain.service.ValidatorHashingService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ValidatorHashingServiceImpl implements ValidatorHashingService {
    @Override
    public String generateBlockHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException("Ошибка SHA-256", noSuchAlgorithmException);
        }
    }
}
