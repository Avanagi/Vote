package com.example.blockchain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pollId;

    private String studentId;

    private Long optionId;

    private long timestamp;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private BlockEntity block;

}