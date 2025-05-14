package com.example.vote.service.impl;

import com.example.vote.exception.hash.HashingException;
import com.example.vote.service.SHA256HashingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class SHA256HashingServiceImpl implements SHA256HashingService {
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
            throw new HashingException("Failed to generate SHA-256 hash.", exception);
        }
    }

    @Override
    public boolean validateSHA256Hash(String receivedData, String savedHash) {
        String receivedHash = generateSHA256Hash(receivedData);
        log.info("Received hash: {}", receivedHash);
        log.info("Saved hash: {}", savedHash);
        return receivedHash.equals(savedHash);
    }
}