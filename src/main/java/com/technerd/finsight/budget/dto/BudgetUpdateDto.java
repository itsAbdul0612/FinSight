package com.technerd.finsight.budget.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetUpdateDto {
    private BigDecimal allocatedAmount;
}
