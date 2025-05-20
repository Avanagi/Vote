package com.example.blockchain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "unconfirmed_transactions")
public class UnconfirmedTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pollId;

    private Long studentId;

    private Long optionId;

    private long createdAt;
}
