package com.example.blockchain.service;

import com.example.blockchain.entity.BlockEntity;
import com.example.blockchain.entity.TransactionEntity;
import com.example.blockchain.repository.BlockRepository;
import com.example.blockchain.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    private final BlockRepository blockRepo;
    private final TransactionRepository txRepo;
    private final KeyPairService keyService;

    private final List<TransactionEntity> txPool = new ArrayList<>();

    public BlockchainService(BlockRepository blockRepo, TransactionRepository txRepo, KeyPairService keyService) {
        this.blockRepo = blockRepo;
        this.txRepo = txRepo;
        this.keyService = keyService;
    }

    public void addTransaction(TransactionEntity tx) {
        tx.setTimestamp(System.currentTimeMillis());
        synchronized (txPool) {
            txPool.add(tx);
            logger.info("Добавлена транзакция: {}. Текущий размер пула: {}", tx, txPool.size());
        }
    }

    @Async
    @Transactional
    public void mineBlock() throws Exception {
        List<TransactionEntity> transactions;

        synchronized (txPool) {
            if (txPool.isEmpty()) {
                logger.warn("Пул транзакций пуст. Майнинг невозможен.");
                return;
            }
            transactions = new ArrayList<>(txPool);
            txPool.clear();
        }

        BlockEntity lastBlock = blockRepo.findTopByOrderByIndexDesc().orElse(null);
        String prevHash = lastBlock != null ? lastBlock.getHash() : "0";

        BlockEntity newBlock = new BlockEntity();
        newBlock.setIndex(lastBlock != null ? lastBlock.getIndex() + 1 : 0);
        newBlock.setPreviousHash(prevHash);
        newBlock.setValidator(Base64.getEncoder().encodeToString(keyService.getKeyPair().getPublic().getEncoded()));
        newBlock.setTimestamp(System.currentTimeMillis());

        for (TransactionEntity tx : transactions) {
            tx.setBlock(newBlock);
        }

        String content = generateBlockContent(transactions, prevHash, newBlock.getValidator());
        newBlock.setHash(generateBlockHash(content));
        newBlock.setSignature(signContent(content));
        newBlock.setTransactions(transactions);

        blockRepo.save(newBlock);
        txRepo.saveAll(transactions);

        logger.info("Блок #{} успешно создан и сохранён.", newBlock.getIndex());
    }

    private String generateBlockContent(List<TransactionEntity> transactions, String prevHash, String validator) {
        StringBuilder sb = new StringBuilder();
        logger.info("Начало сериализации транзакций для блока...");

        transactions.forEach(tx -> {
            Long pollId = tx.getPollId();
            Long optionId = tx.getOptionId();
            Long timestamp = tx.getTimestamp();
            sb.append(pollId != null ? pollId : "null")
                    .append(optionId != null ? optionId : "null")
                    .append(timestamp);
            logger.debug("Транзакция: pollId={}, optionId={}, timestamp={}", pollId, optionId, timestamp);
        });

        sb.append(prevHash).append(validator);

        logger.info("Сериализация завершена. prevHash={}, validator={}", prevHash, validator);

        return sb.toString();
    }

    private String generateBlockHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка SHA-256", e);
        }
    }

    private String signContent(String content) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(keyService.getKeyPair().getPrivate());
        sig.update(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sig.sign());
    }

    public boolean verifySignature(String content, String signature, String publicKeyBase64) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        sig.initVerify(publicKey);
        sig.update(content.getBytes(StandardCharsets.UTF_8));
        return sig.verify(Base64.getDecoder().decode(signature));
    }

    public Map<Long, Long> getResults(Long pollId) {
        logger.info("Получение результатов для опроса с ID: {}", pollId);
        return txRepo.findByPollId(pollId).stream()
                .collect(Collectors.groupingBy(TransactionEntity::getOptionId, Collectors.counting()));
    }

    public boolean isBlockchainValid() {
        List<BlockEntity> blocks = blockRepo.findAllByOrderByIndexAsc();

        for (int i = 0; i < blocks.size(); i++) {
            BlockEntity block = blocks.get(i);
            String expectedContent = generateBlockContent(block.getTransactions(),
                    i > 0 ? blocks.get(i - 1).getHash() : "0",
                    block.getValidator());

            String expectedHash = generateBlockHash(expectedContent);

            if (!expectedHash.equals(block.getHash())) {
                logger.error("Неверный хеш у блока #{}. Ожидался: {}, получен: {}", block.getIndex(), expectedHash, block.getHash());
                return false;
            }

            try {
                if (!verifySignature(expectedContent, block.getSignature(), block.getValidator())) {
                    logger.error("Подпись блока #{} недействительна.", block.getIndex());
                    return false;
                }
            } catch (Exception e) {
                logger.error("Ошибка верификации подписи блока #{}: {}", block.getIndex(), e.getMessage());
                return false;
            }

            if (i > 0 && !block.getPreviousHash().equals(blocks.get(i - 1).getHash())) {
                logger.error("Цепочка нарушена между блоками #{} и #{}", blocks.get(i - 1).getIndex(), block.getIndex());
                return false;
            }
        }

        logger.info("Блокчейн прошел проверку на целостность.");
        return true;
    }
}