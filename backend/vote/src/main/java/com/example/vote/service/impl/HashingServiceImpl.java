package com.example.vote.service.impl;

import com.example.vote.exception.hash.HashingException;
import com.example.vote.service.HashingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class HashingServiceImpl implements HashingService {
    @Override
    public String generateSHA256Hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new HashingException("Не удалось сгенерировать SHA-256 хэш.", exception);
        }
    }

    @Override
    public boolean validateSHA256Hash(String receivedData, String savedHash) {
        String receivedHash = generateSHA256Hash(receivedData);
        log.debug("Полученный хэш: {}", receivedHash);
        log.debug("Сохраненный хэш: {}", receivedHash);
        return receivedHash.equals(savedHash);
    }
}