package com.technerd.finsight.core.category.dto;

import com.technerd.finsight.core.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(name = "CategoryRequest", description = "Category details used to create or update a category and optionally initialize its budget.")
public class CategoryCreateDto {

    @Schema(description = "Category identifier; generally omitted when creating a category", example = "7")
    private Long id;
    @Schema(description = "Category name", example = "Food")
    private String name;
    @Schema(description = "Optional category description", example = "Meals and groceries")
    private String description;
    @Schema(description = "Whether transactions in the category are income or expenses", example = "EXPENSE")
    private TransactionType transactionType;
    @Schema(description = "Category icon identifier", example = "restaurant")
    private String icon;

    // Budget

    @Schema(description = "Optional amount allocated to this category", example = "500.00")
    private BigDecimal allocatedAmount;
    @Schema(description = "Spent amount used when initializing a budget", example = "0.00")
    private BigDecimal spentAmount =  BigDecimal.ZERO;

}
