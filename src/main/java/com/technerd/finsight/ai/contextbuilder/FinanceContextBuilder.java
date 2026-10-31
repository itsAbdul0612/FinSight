package com.technerd.finsight.ai.contextbuilder;


import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FinanceContextBuilder {

    private List<TransactionResponse> lastFiveTransactions;
    private List<SixMonthsTrendDto> sixMonthsBudgetTrend;
    private BigDecimal monthlySummary;
    private Page<BudgetResponse> budgets;

}
