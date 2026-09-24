package com.technerd.finsight.transaction.dto;

import com.technerd.finsight.category.Category;
import com.technerd.finsight.transaction.Transaction;
import com.technerd.finsight.transaction.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;
    private String icon;
    private String description;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private CategoryResponse category;

    @Data
    public static class CategoryResponse {
        private Long id;
        private String name;

        public static CategoryResponse from(Category c) {
            if (c == null) return null;
            CategoryResponse cr = new CategoryResponse();
            cr.setId(c.getId());
            cr.setName(c.getName());
            return cr;
        }
    }

    public static TransactionResponse from(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(transaction.getId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setTransactionType(transaction.getTransactionType());
        transactionResponse.setTransactionDate(transaction.getTransactionDate());
        transactionResponse.setDescription(transaction.getDescription());
        transactionResponse.setCategory(CategoryResponse.from(transaction.getCategory()));
        return transactionResponse;
    }

}
