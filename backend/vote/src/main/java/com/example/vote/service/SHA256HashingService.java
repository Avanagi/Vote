package com.example.vote.service;

public interface SHA256HashingService {

    String generateSHA256Hash(String text);

    boolean validateSHA256Hash(String receivedData, String savedHash);
}
