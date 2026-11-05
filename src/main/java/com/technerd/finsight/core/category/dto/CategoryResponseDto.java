package com.technerd.finsight.core.category.dto;

import com.technerd.finsight.core.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "CategoryResponse", description = "A category owned by a user.")
public class CategoryResponseDto {

    @Schema(description = "Category identifier", example = "7")
    private Long id;
    @Schema(description = "Category name", example = "Food")
    private String name;
    @Schema(description = "Category description", example = "Meals and groceries")
    private String description;
    @Schema(description = "Whether transactions in the category are income or expenses", example = "EXPENSE")
    private TransactionType transactionType;
    @Schema(description = "Category icon identifier", example = "restaurant")
    private String icon;
}
