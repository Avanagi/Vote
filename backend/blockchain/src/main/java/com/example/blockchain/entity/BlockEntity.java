package com.example.blockchain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blocks")
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactions;

}
