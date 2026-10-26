package com.technerd.finsight.core.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {

    private String description;
    private BigDecimal amount;
    private Long categoryId;
}
