package com.example.blockchain.service.Impl;

import com.example.blockchain.entity.BlockEntity;
import com.example.blockchain.entity.TransactionEntity;
import com.example.blockchain.entity.UnconfirmedTransactionEntity;
import com.example.blockchain.mapper.TransactionsMapper;
import com.example.blockchain.repository.BlockRepository;
import com.example.blockchain.repository.TransactionRepository;
import com.example.blockchain.repository.UnconfirmedTransactionRepository;
import com.example.blockchain.service.BlockchainService;
import com.example.blockchain.service.ValidatorHashingService;
import com.example.blockchain.service.ValidatorSigningService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Service
@EnableScheduling
public class BlockchainServiceImpl implements BlockchainService {

    private final BlockRepository blockRepo;
    private final TransactionRepository transactionRepository;
    private final ValidatorHashingService validatorHashingService;
    private final ValidatorSigningService validatorSigningService;
    private final UnconfirmedTransactionRepository unconfirmedTransactionRepository;
    private final TransactionsMapper transactionsMapper;

    private final Set<TransactionEntity> transactionPool = new HashSet<>();
    private volatile boolean isBlockchainHealthy = true;

    public BlockchainServiceImpl(BlockRepository blockRepo,
                                 TransactionRepository transactionRepository,
                                 ValidatorHashingService validatorHashingService,
                                 ValidatorSigningService validatorSigningService,
                                 UnconfirmedTransactionRepository unconfirmedTransactionRepository, TransactionsMapper transactionsMapper) {
        this.blockRepo = blockRepo;
        this.transactionRepository = transactionRepository;
        this.validatorHashingService = validatorHashingService;
        this.validatorSigningService = validatorSigningService;
        this.unconfirmedTransactionRepository = unconfirmedTransactionRepository;
        this.transactionsMapper = transactionsMapper;
    }

    @PostConstruct
    public void initPoolFromUnconfirmed() {
        List<UnconfirmedTransactionEntity> unconfirmed = unconfirmedTransactionRepository.findAll();
        List<TransactionEntity> restored = unconfirmed.stream()
                .map(transactionsMapper::toConfirmed)
                .toList();

        synchronized (transactionPool) {
            transactionPool.addAll(restored);
        }
        log.info("Инициализирован пул из {} неподтвержденных транзакций.", restored.size());
    }

    @Transactional
    public void addTransaction(TransactionEntity transactionEntity) {
        transactionEntity.setCreatedAt(System.currentTimeMillis());

        UnconfirmedTransactionEntity unconfirmed = transactionsMapper.toUnconfirmed(transactionEntity);
        unconfirmedTransactionRepository.save(unconfirmed);

        if (!isBlockchainHealthy) {
            log.warn("Транзакция не добавлена в пул: блокчейн невалиден.");
            return;
        }

        synchronized (transactionPool) {
            transactionPool.add(transactionEntity);
            log.info("Добавлена транзакция. Текущий размер пула: {}", transactionPool.size());
        }
    }

    @Async
    @Transactional
    public void mineBlock() {
        if (!isBlockchainHealthy) {
            log.warn("Майнинг невозможен: блокчейн невалиден.");
            return;
        }

        List<TransactionEntity> transactions;
        synchronized (transactionPool) {
            if (transactionPool.isEmpty()) {
                log.debug("Пул транзакций пуст. Майнинг невозможен. Время: {}", LocalDateTime.now());
                return;
            }
            transactions = new ArrayList<>(transactionPool);
            transactionPool.clear();
        }

        BlockEntity lastBlock = blockRepo.findTopByOrderByIndexDesc().orElse(null);
        String prevHash = lastBlock != null ? lastBlock.getHash() : "0";

        BlockEntity newBlock = new BlockEntity();
        newBlock.setIndex(lastBlock != null ? lastBlock.getIndex() + 1 : 0);
        newBlock.setPreviousHash(prevHash);
        newBlock.setValidator(validatorSigningService.getPublicKeyBase64());

        for (TransactionEntity transactionEntity : transactions) {
            transactionEntity.setBlock(newBlock);
        }

        String content = generateBlockContent(transactions, prevHash, newBlock.getValidator());
        newBlock.setHash(validatorHashingService.generateBlockHash(content));
        newBlock.setSignature(validatorSigningService.signContent(content));
        newBlock.setTransactions(transactions);

        blockRepo.save(newBlock);
        transactionRepository.saveAll(transactions);

        for (TransactionEntity transactionEntity : transactions) {
            unconfirmedTransactionRepository
                    .deleteByPollIdAndStudentId(transactionEntity.getPollId(), transactionEntity.getStudentId());
        }

        log.info("Блок #{} успешно создан. Удалено неподтвержденных: {}", newBlock.getIndex() + 1, transactions.size());
    }

    @Async
    @Transactional
    public void mineUnconfirmedTransactions() {
        if (!isBlockchainHealthy) {
            log.warn("Ошибка майнинга неподтвержденных транзакций: блокчейн невалиден.");
            return;
        }

        List<UnconfirmedTransactionEntity> unconfirmed = unconfirmedTransactionRepository.findAll();
        if (unconfirmed.isEmpty()) {
            log.debug("Нет неподтверждённых транзакций для майнинга.");
            return;
        }

        List<TransactionEntity> transactionEntities = unconfirmed.stream()
                .map(transactionsMapper::toConfirmed)
                .toList();

        log.info("Обнаружено {} неподтверждённых транзакций. Запуск майнинга...", transactionEntities.size());

        synchronized (transactionPool) {
            transactionPool.addAll(transactionEntities);
        }

        try {
            mineBlock();
        } catch (Exception exception) {
            log.error("Ошибка при майнинге: {}", exception.getMessage(), exception);
        }
    }

    @Transactional
    public Map<Long, Long> getResults(Long pollId) {
        log.info("Получение результатов для опроса с ID: #{}", pollId);
        return transactionRepository.findByPollId(pollId).stream()
                .collect(Collectors.groupingBy(TransactionEntity::getOptionId, Collectors.counting()));
    }

    @Override
    @Transactional
    public Map<Long, Long> getStudentAnswers(Long studentId) {
        log.info("Получение ответов студента с ID: #{}", studentId);
        return transactionRepository.findByStudentId(studentId).stream()
                .collect(Collectors.toMap(
                        TransactionEntity::getPollId,
                        TransactionEntity::getOptionId,
                        (existing, duplicate) -> existing
                ));
    }


    @Transactional
    public boolean isBlockchainValid() {
        List<BlockEntity> blocks = blockRepo.findAllByOrderByIndexAsc();

        for (int i = 0; i < blocks.size(); i++) {
            BlockEntity block = blocks.get(i);
            String expectedContent = generateBlockContent(block.getTransactions(),
                    i > 0 ? blocks.get(i - 1).getHash() : "0",
                    block.getValidator());

            String expectedHash = validatorHashingService.generateBlockHash(expectedContent);

            if (!expectedHash.equals(block.getHash())) {
                log.error("Неверный хеш у блока #{}. Ожидался: {}, получен: {}",
                        block.getIndex(), expectedHash, block.getHash());
                return false;
            }

            try {
                if (!validatorSigningService.verifySignature(expectedContent, block.getSignature(), block.getValidator())) {
                    log.error("Подпись блока #{} недействительна.", block.getIndex());
                    return false;
                }
            } catch (Exception exception) {
                log.error("Ошибка верификации подписи блока #{}: {}", block.getIndex(), exception.getMessage());
                return false;
            }

            if (i > 0 && !block.getPreviousHash().equals(blocks.get(i - 1).getHash())) {
                log.error("Цепочка нарушена между блоками #{} и #{}", blocks.get(i - 1).getIndex(), block.getIndex());
                return false;
            }
        }

        log.debug("Блокчейн прошел проверку на целостность. Время: {}", LocalDateTime.now());
        return true;
    }

    private String generateBlockContent(List<TransactionEntity> transactions, String prevHash, String validator) {
        StringBuilder stringBuilder = new StringBuilder();

        transactions.forEach(transactionEntity -> {
            Long pollId = transactionEntity.getPollId();
            Long optionId = transactionEntity.getOptionId();
            Long studentId = transactionEntity.getStudentId();
            Long createdAt = transactionEntity.getCreatedAt();
            stringBuilder.append(pollId != null ? pollId : "null")
                    .append(optionId != null ? optionId : "null")
                    .append(studentId != null ? studentId : "null")
                    .append(createdAt);
            log.debug("Транзакция: pollId={}, optionId={}, studentId={}, createdAt={}",
                    pollId, optionId, studentId, createdAt);
        });

        stringBuilder.append(prevHash).append(validator);

        log.debug("Сериализация завершена. prevHash={}, validator={}",
                prevHash, validator);

        return stringBuilder.toString();
    }

}
