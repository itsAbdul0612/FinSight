package com.technerd.finsight.ai.contextbuilder;

import com.technerd.finsight.analytics.AnalyticsService;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;

@Slf4j
@RequiredArgsConstructor
@Service
public class FinanceContextBuilderService {

    private final TransactionService transactionService;
    private final AnalyticsService analyticsService;
    private final BudgetService budgetService;

    @Caching(evict = {
            @CacheEvict(cacheNames = "all_budget", key = "#userId"),
            @CacheEvict(cacheNames = "monthly_summary", key = "#userId")
    })
    @Cacheable(cacheNames = "finance_context", key = "#userId")
    public FinanceContextBuilderDto createFinanceContext(Long userId){

        List<TransactionResponse> lastTenTransactions = transactionService.lastTenResponse(userId);
        BigDecimal monthlySummary = analyticsService.monthlySummary(userId, EXPENSE, YearMonth.now().toString());
        List<BudgetResponse> allBudget = budgetService.findAllByUserIdAndMonth(userId, YearMonth.now().toString());
        List<BudgetResponse> currentMonthBudget = budgetService.findAllByUserIdAndMonth(
                userId,
                YearMonth.now().toString()
        );

        log.info("Created finance context for user: {}", userId);
        return FinanceContextBuilderDto.builder()
                .lastTenTransactions(lastTenTransactions)
                .monthlySummary(monthlySummary)
                .allBudget(allBudget)
                .budgets(currentMonthBudget)
                .build();
    }


}
