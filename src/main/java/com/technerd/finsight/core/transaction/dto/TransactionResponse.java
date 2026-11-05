package com.technerd.finsight.core.transaction.dto;

import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.transaction.Transaction;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(name = "TransactionResponse", description = "A transaction returned by the API, including its category summary.")
public class TransactionResponse implements Serializable {

    @Schema(description = "Transaction identifier", example = "103")
    private Long id;
    @Schema(description = "Icon identifier for the transaction category", example = "restaurant")
    private String icon;
    @Schema(description = "Transaction description", example = "Lunch")
    private String description;
    @Schema(description = "Whether this is income or an expense", example = "EXPENSE")
    private TransactionType transactionType;
    @Schema(description = "Transaction amount", example = "24.95")
    private BigDecimal amount;
    @Schema(description = "Transaction date and time", example = "2026-11-06T12:30:00")
    private LocalDateTime transactionDate;
    @Schema(description = "Whether the transaction is soft-deleted")
    private Boolean isDeleted;
    @Schema(description = "Whether the transaction caused a budget breach")
    private Boolean isBreached;


    @Schema(description = "Category summary for the transaction")
    private CategoryResponse category;

    @Data
    @Schema(name = "TransactionCategoryResponse", description = "Category details included with a transaction.")
    public static class CategoryResponse implements Serializable {
        // Can centralize this class to reduce redundancy
        @Schema(description = "Category identifier", example = "7")
        private Long id;
        @Schema(description = "Category icon identifier", example = "restaurant")
        private String icon;
        @Schema(description = "Category name", example = "Food")
        private String name;

        public static CategoryResponse from(Category category) {
            if (category == null) return null;
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(category.getId());
            categoryResponse.setName(category.getName());
            categoryResponse.setIcon(category.getIcon());
            return categoryResponse;
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
        transactionResponse.setIcon(CategoryResponse.from(transaction.getCategory()).getIcon());
        return transactionResponse;
    }

}
