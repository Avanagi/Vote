package com.example.blockchain.scheduler;

import com.example.blockchain.service.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BlockchainScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainScheduler.class);

    private final BlockchainService blockchainService;

    public BlockchainScheduler(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Scheduled(fixedRate = 60000)
    public void proposeNewBlock() throws Exception {
        blockchainService.mineBlock();
    }
}