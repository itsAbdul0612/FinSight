package com.technerd.finsight.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TopMonthlyExpense", description = "Projection of a high-spend category for a month.")
public interface TopExpenseOfAMonth {
    @Schema(description = "Month in ISO year-month format (yyyy-MM)", example = "2026-11")
    String getMonth();
    @Schema(description = "Category name", example = "Food")
    String getCategoryName();
    @Schema(description = "Total amount spent in the category for the month", example = "620.00")
    BigDecimal getTotalSpent();
}