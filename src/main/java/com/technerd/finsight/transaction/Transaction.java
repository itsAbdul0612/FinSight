package com.technerd.finsight.transaction;

import com.technerd.finsight.category.Category;
import com.technerd.finsight.transaction.enums.TransactionType;
import com.technerd.finsight.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal amount =  BigDecimal.ZERO;

    private LocalDateTime transactionDate;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User user;

}
