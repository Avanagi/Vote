package com.example.blockchain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "blocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int index;

    @Column(name = "previous_hash", length = 1024)
    private String previousHash;

    @Column(length = 1024, unique = true)
    private String hash;

    @Column(name = "proposer_public_key", length = 1024)
    private String validator;

    @Column(length = 2048)
    private String signature;

    private long timestamp;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockEntity block = (BlockEntity) o;
        return index == block.index && timestamp == block.timestamp && Objects.equals(id, block.id) && Objects.equals(previousHash, block.previousHash) && Objects.equals(hash, block.hash) && Objects.equals(validator, block.validator) && Objects.equals(signature, block.signature) && Objects.equals(transactions, block.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, index, previousHash, hash, validator, signature, timestamp, transactions);
    }
}
