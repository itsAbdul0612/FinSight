package com.technerd.finsight.analytics;

import java.math.BigDecimal;

public record CategorySpendDto(String categoryName, BigDecimal spendAmount) {
}
