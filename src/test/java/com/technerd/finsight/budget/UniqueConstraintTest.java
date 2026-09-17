package com.technerd.finsight.budget;

import org.hibernate.annotations.Audited;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.YearMonth;

@SpringBootTest
public class UniqueConstraintTest {

    @Autowired
    private BudgetService  budgetService;

    @Disabled
    @Test
    void uniqueConstraintTest(){
        Budget byUserAndCategoryAndMonth = budgetService.findByUserAndCategoryAndMonth(1L, 1L, YearMonth.now().toString());
        System.out.println(byUserAndCategoryAndMonth.getMonth());
    }
}
