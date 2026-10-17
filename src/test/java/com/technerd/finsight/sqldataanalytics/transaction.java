package com.technerd.finsight.sqldataanalytics;

import com.technerd.finsight.analytics.AnalyticsRepository;
import com.technerd.finsight.analytics.CategorySpendDto;
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


    @Test
    void spendingByCategory() {
        List<CategorySpendDto> expense = analyticsRepository.totalMonthlySpendingByMonthAndCategory(1L, EXPENSE, "2026-09");

        for (CategorySpendDto value : expense) {
            System.out.println(value.categoryName());
            System.out.println(value.spendAmount());
        }
    }

}
