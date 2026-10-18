package com.technerd.finsight.analytics.dto;

import java.math.BigDecimal;

public record CategorySpendDto(String categoryName, BigDecimal spendAmount) {
 // This is a projection to get the required data from DB and create analytics based on category wise monthly spending.
}
