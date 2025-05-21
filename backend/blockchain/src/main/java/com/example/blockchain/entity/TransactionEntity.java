package com.example.blockchain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pollId;

    private Long studentId;

    private Long optionId;

    private long createdAt;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private BlockEntity block;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return createdAt == that.createdAt && Objects.equals(id, that.id) && Objects.equals(pollId, that.pollId)
                && Objects.equals(studentId, that.studentId) && Objects.equals(optionId, that.optionId)
                && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pollId, studentId, optionId, createdAt, block);
    }
}