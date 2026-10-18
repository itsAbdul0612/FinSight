package com.technerd.finsight.sqldataanalytics;

import com.technerd.finsight.analytics.AnalyticsRepository;
import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.MonthTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static com.technerd.finsight.transaction.enums.TransactionType.EXPENSE;


@SpringBootTest
public class transaction {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Disabled
    @Test
    void totalMonthlyTransactionsByTypeAndMonth() {
        BigDecimal bigDecimal = analyticsRepository.monthlySummary(1L, "EXPENSE", "2026-10");
        System.out.println(bigDecimal);
    }

    @Disabled
    @Test
    void spendingByCategory() {
        List<CategorySpendDto> expense = analyticsRepository.totalMonthlySpendingByMonthAndCategory(1L, EXPENSE, "2026-09");

        for (CategorySpendDto value : expense) {
            System.out.println(value.categoryName());
            System.out.println(value.spendAmount());
        }
    }

    @Disabled
    @Test
    void SixMonthTrend(){
        List<MonthTrendDto> trend = analyticsRepository.trend(EXPENSE, 1L);
        for (MonthTrendDto value : trend) {
            System.out.println(value.month()+ ": " +value.amountSpent());
        }
    }

    @Disabled
    @Test
    void topExpensesAMonth(){
        List<TopExpenseOfAMonth> topExpenseOfAMonths = analyticsRepository.topExpensesByMonth(
                1L,
                "2026-09",
                EXPENSE,
                5
        );

        for (TopExpenseOfAMonth value : topExpenseOfAMonths) {
            System.out.println(value.getMonth());
            System.out.println(value.getCategoryName() + ": "+ value.getTotalSpent());
        }

    }

}
