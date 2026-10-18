package com.technerd.finsight.analytics.dto;

import java.math.BigDecimal;

public record MonthTrendDto(String month, BigDecimal amountSpent) {
}
