package com.example.blockchain.mapper;

import com.example.blockchain.entity.TransactionEntity;
import com.example.blockchain.entity.UnconfirmedTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionsMapper {

    UnconfirmedTransactionEntity toUnconfirmed(TransactionEntity transactionEntity);

    @Mapping(target = "id", ignore = true)
    TransactionEntity toConfirmed(UnconfirmedTransactionEntity unconfirmed);
}
