package com.technerd.finsight.category;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetRepository;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.UserService;
import com.technerd.finsight.transaction.Transaction;
import com.technerd.finsight.transaction.TransactionRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static com.technerd.finsight.transaction.enums.TransactionType.EXPENSE;

@Disabled
@SpringBootTest
public class EntityFlowTest {

    @Autowired
    private  CategoryService categoryService;
    @Autowired
    private  UserService userService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetRepository  budgetRepository;
    @Autowired
    private TransactionRepository tRepository;

    @Disabled
    @Test
    void theFlow() {
        User user = userService.findByEmail("itsabdul0612@gmail.com");

        Category food = Category.builder()
                .icon("F")
                .name("Food")
                .isDefault(true)
                .user(user)
                .build();

        Budget budget = Budget.builder()
                .category(food)
                .user(user)
                .isBreached(false)
                .allocatedAmount(BigDecimal.valueOf(2000))
                .month(YearMonth.now())
                .spentAmount(BigDecimal.valueOf(100))
                .build();

        Transaction transaction = Transaction.builder()
                .description("Ate shawarma")
                .transactionType(EXPENSE)
                .amount(BigDecimal.valueOf(100))
                .transactionDate(LocalDateTime.now())
                .category(food)
                .user(user)
                .build();

        categoryRepository.save(food);
        budgetRepository.save(budget);
        tRepository.save(transaction);

        System.out.println("Done");
    }


}
