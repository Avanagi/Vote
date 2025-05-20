package com.example.blockchain.service;

import java.io.File;
import java.security.KeyPair;

public interface ValidatorKeyPairService {

    KeyPair generateKeyPair();

    void saveKeyPair(KeyPair keyPair, File privateKeyFile, File publicKeyFile);

    KeyPair loadKeyPair(File privateKeyFile, File publicKeyFile);

    KeyPair getKeyPair();
}
