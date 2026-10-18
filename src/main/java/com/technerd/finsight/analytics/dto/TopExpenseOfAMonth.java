package com.technerd.finsight.analytics.dto;

import java.math.BigDecimal;

public interface TopExpenseOfAMonth {
    String getMonth();
    String getCategoryName();
    BigDecimal getTotalSpent();
}