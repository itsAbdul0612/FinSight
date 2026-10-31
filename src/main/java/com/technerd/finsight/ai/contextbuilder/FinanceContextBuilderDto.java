package com.technerd.finsight.ai.contextbuilder;

import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FinanceContextBuilderDto {

    private List<TransactionResponse> lastTenTransactions;
    private List<BudgetResponse> allBudget;
    private BigDecimal monthlySummary;
    private List<BudgetResponse> budgets;

}
