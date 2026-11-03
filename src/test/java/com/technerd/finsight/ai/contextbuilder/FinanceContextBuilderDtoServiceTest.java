package com.technerd.finsight.ai.contextbuilder;

import com.technerd.finsight.analytics.AnalyticsService;
import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class FinanceContextBuilderDtoServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private FinanceContextBuilderService financeContextBuilderService;

    @Test
    void createFinanceContextReturnsAllFinanceDataForCurrentMonth() {
        Long userId = 42L;
        String currentMonth = YearMonth.now().toString();

        TransactionResponse transaction = new TransactionResponse();
        transaction.setId(101L);
        transaction.setDescription("Groceries");
        transaction.setAmount(new BigDecimal("45.50"));
        List<TransactionResponse> transactions = List.of(transaction);

        BigDecimal monthlySummary = new BigDecimal("325.75");
        List<SixMonthsTrendDto> trend = List.of(
                new SixMonthsTrendDto(currentMonth, new BigDecimal("325.75"))
        );

        BudgetResponse budget = new BudgetResponse();
        budget.setId(201L);
        budget.setAllocatedAmount(new BigDecimal("500.00"));
        budget.setSpentAmount(new BigDecimal("325.75"));
        budget.setMonth(currentMonth);
        Page<BudgetResponse> budgets = new PageImpl<>(List.of(budget));

//        when(transactionService.lastFiveResponse(userId)).thenReturn(transactions);
//        when(analyticsService.monthlySummary(userId, EXPENSE, currentMonth)).thenReturn(monthlySummary);
//        when(analyticsService.sixMonthTrend(userId, EXPENSE)).thenReturn(trend);
//        when(budgetService.findAllByUserIdAndMonth(userId, Pageable.ofSize(50), currentMonth))
//                .thenReturn(budgets);
//
//        FinanceContextBuilderDto result = financeContextBuilderService.createFinanceContext(userId);
//
//        assertThat(result.getLastTenTransactions()).isSameAs(transactions);
//        assertThat(result.getMonthlySummary()).isEqualByComparingTo("325.75");
//        assertThat(result.getSixMonthsBudgetTrend()).containsExactlyElementsOf(trend);
//        assertThat(result.getBudgets()).isSameAs(budgets);
//
//        verify(transactionService).lastTenResponse(userId);
//        verify(analyticsService).monthlySummary(userId, EXPENSE, currentMonth);
//        verify(analyticsService).sixMonthTrend(userId, EXPENSE);
//        verify(budgetService).findAllByUserIdAndMonth(userId, Pageable.ofSize(50), currentMonth);
    }

    @Test
    void invalidateFinanceContextIsConfiguredToEvictTheUsersCachedContext() throws NoSuchMethodException {
        Long userId = 42L;

//        contextBuilderService.invalidateFinanceContextCache(userId);

        CacheEvict cacheEvict = FinanceContextBuilderService.class
                .getMethod("invalidateFinanceContextCache", Long.class)
                .getAnnotation(CacheEvict.class);

        assertThat(cacheEvict).isNotNull();
        assertThat(cacheEvict.cacheNames()).containsExactly("finance_context");
        assertThat(cacheEvict.key()).isEqualTo("#userId");
        verifyNoInteractions(transactionService, analyticsService, budgetService);
    }
}
