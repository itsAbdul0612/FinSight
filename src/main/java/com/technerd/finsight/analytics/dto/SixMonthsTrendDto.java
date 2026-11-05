package com.technerd.finsight.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "MonthlySpendingTrend", description = "Spending total for one month in a six-month trend.")
public record SixMonthsTrendDto(
        @Schema(description = "Month in ISO year-month format (yyyy-MM)", example = "2026-11") String month,
        @Schema(description = "Amount spent during the month", example = "1250.75") BigDecimal amountSpent) {
}
