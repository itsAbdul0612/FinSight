package com.technerd.finsight.analytics;

import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final BudgetService budgetService;

    @Cacheable(cacheNames = "monthly-summary", key = "#userId")
    public BigDecimal monthlySummary(Long userId, TransactionType type, String month) {
    return analyticsRepository.monthlySummary(userId, type.toString(), month);
    }

    public List<CategorySpendDto> totalMonthlySpendingOfACategory(Long userId, TransactionType type, String month){
       return analyticsRepository.totalMonthlySpendingByMonthAndCategory(userId, type, month);
    }

    public List<SixMonthsTrendDto> sixMonthTrend(Long userId, TransactionType type){
        return analyticsRepository.trend(type, userId);
    }

    public List<BudgetResponse> allBudgets(Long userId, String month){
        return budgetService.findAllByUserIdAndMonth(userId,  month);
    }

    public List<TopExpenseOfAMonth> topExpensesOfAMonths(Long userId, String month,TransactionType type, Integer limit){
        return analyticsRepository.topExpensesByMonth(userId, month, type, limit);
    }

}
