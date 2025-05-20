package com.example.blockchain.service.Impl;

import com.example.blockchain.service.ValidatorKeyPairService;
import com.example.blockchain.service.ValidatorSigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@AllArgsConstructor
public class ValidatorSigningServiceImpl implements ValidatorSigningService {

    private final ValidatorKeyPairService validatorKeyPairService;

    @Override
    public String signContent(String content) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(validatorKeyPairService.getKeyPair().getPrivate());
            sig.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (NoSuchAlgorithmException |
                 InvalidKeyException | SignatureException securityException) {
            throw new RuntimeException(securityException);
        }
    }

    @Override
    public boolean verifySignature(String content, String signature, String publicKeyBase64) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            sig.initVerify(publicKey);
            sig.update(content.getBytes(StandardCharsets.UTF_8));
            return sig.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException |
                 InvalidKeyException | SignatureException securityException) {
            throw new RuntimeException(securityException);
        }
    }

    @Override
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(validatorKeyPairService.getKeyPair().getPublic().getEncoded());
    }
}
