package com.technerd.finsight.entity;

import com.technerd.finsight.entity.enums.TransactionType;
import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private LocalDateTime transactionDate;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User user;

}
