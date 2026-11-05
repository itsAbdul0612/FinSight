package com.technerd.finsight.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "CategorySpend", description = "Total spending for a category in a month.")
public record CategorySpendDto(
        @Schema(description = "Category name", example = "Food") String categoryName,
        @Schema(description = "Total amount spent in the category", example = "320.50") BigDecimal spendAmount) {
 // This is a projection to get the required data from DB and create analytics based on category wise monthly spending.
}
