package com.technerd.finsight.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;
    private String icon;
    private String description;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private CategoryResponse category;

    @Data
    public static class CategoryResponse {
        private Long id;
        private String name;
    }

}
