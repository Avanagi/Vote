package com.example.blockchain.service;

import com.example.blockchain.entity.TransactionEntity;

import java.util.Map;

public interface BlockchainService {

    void addTransaction(TransactionEntity tx);

    void mineBlock();

    void mineUnconfirmedTransactions();

    Map<Long, Long> getResults(Long pollId);

    boolean isBlockchainValid();

    void setBlockchainHealthy(boolean valid);
}
