package com.technerd.finsight.sqldataanalytics;

import com.technerd.finsight.analytics.AnalyticsRepository;
import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.MonthTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Transaction Analytics Query Tests")
public class Transaction {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Test
    @DisplayName("Should calculate total monthly transactions by type and month")
    void totalMonthlyTransactionsByTypeAndMonth() {
        BigDecimal totalExpense = analyticsRepository.monthlySummary(1L, "EXPENSE", "2026-10");

        assertThat(totalExpense)
                .as("Monthly summary expense should not be null")
                .isNotNull()
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should retrieve monthly spending grouped by category")
    void spendingByCategory() {
        List<CategorySpendDto> expenseList = analyticsRepository.totalMonthlySpendingByMonthAndCategory(1L, EXPENSE, "2026-09");

        assertThat(expenseList)
                .as("Category spending list should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        assertThat(expenseList).allSatisfy(dto -> {
            assertThat(dto.categoryName()).isNotBlank();
            assertThat(dto.spendAmount()).isNotNull().isGreaterThan(BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should retrieve six month expense trend")
    void sixMonthTrend() {
        List<MonthTrendDto> trendList = analyticsRepository.trend(EXPENSE, 1L);

        assertThat(trendList)
                .as("Six month trend list should not be null or empty")
                .isNotNull()
                .isNotEmpty()
                .hasSizeLessThanOrEqualTo(6);

        assertThat(trendList).allSatisfy(dto -> {
            assertThat(dto.month()).matches("^\\d{4}-\\d{2}$");
            assertThat(dto.amountSpent()).isNotNull().isGreaterThanOrEqualTo(BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should retrieve top expenses for a month limited to specified count")
    void topExpensesAMonth() {
        int limit = 5;
        List<TopExpenseOfAMonth> topExpenses = analyticsRepository.topExpensesByMonth(
                1L,
                "2026-09",
                EXPENSE,
                limit
        );

        assertThat(topExpenses)
                .as("Top expenses should not be null or empty")
                .isNotNull()
                .isNotEmpty()
                .hasSizeLessThanOrEqualTo(limit);

        assertThat(topExpenses).allSatisfy(dto -> {
            assertThat(dto.getMonth()).isEqualTo("2026-09");
            assertThat(dto.getCategoryName()).isNotBlank();
            assertThat(dto.getTotalSpent()).isNotNull().isGreaterThan(BigDecimal.ZERO);
        });

        assertThat(topExpenses)
                .as("Top expenses should be sorted descending by total spent")
                .isSortedAccordingTo((a, b) -> b.getTotalSpent().compareTo(a.getTotalSpent()));
    }
}
