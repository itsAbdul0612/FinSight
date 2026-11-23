package com.technerd.finsight.analytics;

import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock private AnalyticsRepository analyticsRepository;
    @Mock private BudgetService budgetService;

    @Test
    void monthlySummaryDelegatesToRepository() {
        when(analyticsRepository.monthlySummary(1L, "EXPENSE", "2026-11"))
                .thenReturn(new BigDecimal("250"));

        assertThat(new AnalyticsService(analyticsRepository, budgetService)
                .monthlySummary(1L, TransactionType.EXPENSE, "2026-11"))
                .isEqualByComparingTo("250");
    }

    @Test
    void categorySpendingDelegatesToRepository() {
        List<CategorySpendDto> result = List.of(new CategorySpendDto("Food", new BigDecimal("100")));
        when(analyticsRepository.totalMonthlySpendingByMonthAndCategory(
                1L, TransactionType.EXPENSE, "2026-11")).thenReturn(result);

        assertThat(new AnalyticsService(analyticsRepository, budgetService)
                .totalMonthlySpendingOfACategory(1L, TransactionType.EXPENSE, "2026-11"))
                .isEqualTo(result);
    }

    @Test
    void allBudgetsDelegatesToBudgetService() {
        new AnalyticsService(analyticsRepository, budgetService).allBudgets(1L, "2026-11");
        verify(budgetService).findAllByUserIdAndMonth(1L, "2026-11");
    }
}
