package com.technerd.finsight.ai.contextbuilder;

import com.technerd.finsight.analytics.AnalyticsService;
import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContextBuilderService {

    private final TransactionService transactionService;
    private final AnalyticsService analyticsService;
    private final BudgetService budgetService;

    @Autowired
    private static CacheManager cacheManager;


    @Cacheable(cacheNames = "finance_context", key = "#userId")
    public FinanceContextBuilder createFinanceContext(Long userId){

        List<TransactionResponse> lastFiveTransactions = transactionService.lastFiveResponse(userId);
        BigDecimal monthlySummary = analyticsService.monthlySummary(userId, EXPENSE, YearMonth.now().toString());
        List<SixMonthsTrendDto> sixMonthsTrend = analyticsService.sixMonthTrend(userId, EXPENSE);
        Page<BudgetResponse> currentMonthBudget = budgetService.findAllByUserIdAndMonth(
                userId,
                Pageable.ofSize(50),
                YearMonth.now().toString()
        );

        log.info("Created finance context for user: {}", userId);
        return FinanceContextBuilder.builder()
                .lastFiveTransactions(lastFiveTransactions)
                .monthlySummary(monthlySummary)
                .sixMonthsBudgetTrend(sixMonthsTrend)
                .budgets(currentMonthBudget)
                .build();
    }


}
