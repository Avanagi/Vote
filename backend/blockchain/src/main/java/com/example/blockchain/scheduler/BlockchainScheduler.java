package com.example.blockchain.scheduler;

import com.example.blockchain.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BlockchainScheduler {
    private final BlockchainService blockchainService;

    public BlockchainScheduler(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Scheduled(fixedRate = 60000)
    public void mineBlock() {
        blockchainService.mineBlock();
    }

    @Scheduled(fixedRate = 15000)
    public void validateBlockchain() {
        boolean valid = blockchainService.isBlockchainValid();
        blockchainService.setBlockchainHealthy(valid);

        if(!valid) {
            log.error("Целостность блокчейна нарушена! Система переведена в защищённый режим.");
        } else {
            log.debug("Проверка блокчейна завершена успешно.");
        }
    }

    @Scheduled(fixedRate = 30000)
    public void autoMineBlock() {
        blockchainService.mineUnconfirmedTransactions();
    }
}