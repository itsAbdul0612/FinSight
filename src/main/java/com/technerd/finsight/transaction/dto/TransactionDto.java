package com.technerd.finsight.transaction.dto;

import com.technerd.finsight.transaction.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {

    private String description;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long categoryId;
}
