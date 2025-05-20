package com.example.blockchain.service;

public interface ValidatorSigningService {

    String signContent(String content);

    boolean verifySignature(String content, String signature, String publicKeyBase64);

    String getPublicKeyBase64();

}
