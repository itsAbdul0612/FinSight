package com.technerd.finsight.core;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.budget.BudgetRepository;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryRepository;
import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.transaction.Transaction;
import com.technerd.finsight.core.transaction.TransactionRepository;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static com.technerd.finsight.core.transaction.enums.TransactionType.EXPENSE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CoreFlowTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private TransactionRepository tRepository;

    @Test
    @DisplayName("Should execute core flow by creating category, budget, and transaction")
    void testCoreFlow() {
        User user = userSecurityService.findByEmail("itsabdul0612@gmail.com");
        if (user == null) {
            user = userSecurityService.findById(1L);
        }
        assertThat(user).as("User must exist for core flow test").isNotNull();

        Category food = Category.builder()
                .icon("F")
                .name("Food Test Core Flow")
                .transactionType(EXPENSE)
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(food);
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Food Test Core Flow");
        assertThat(savedCategory.getUser().getId()).isEqualTo(user.getId());

        String currentMonth = YearMonth.now().toString();
        Budget budget = Budget.builder()
                .category(savedCategory)
                .user(user)
                .isBreached(false)
                .allocatedAmount(BigDecimal.valueOf(2000))
                .month(currentMonth)
                .spentAmount(BigDecimal.valueOf(100))
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        assertThat(savedBudget).isNotNull();
        assertThat(savedBudget.getId()).isNotNull();
        assertThat(savedBudget.getAllocatedAmount()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(savedBudget.getSpentAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(savedBudget.getIsBreached()).isFalse();
        assertThat(savedBudget.getMonth()).isEqualTo(currentMonth);
        assertThat(savedBudget.getCategory().getId()).isEqualTo(savedCategory.getId());

        Transaction transaction = Transaction.builder()
                .description("Ate shawarma")
                .transactionType(EXPENSE)
                .amount(BigDecimal.valueOf(100))
                .transactionDate(LocalDateTime.now())
                .category(savedCategory)
                .user(user)
                .isDeleted(false)
                .build();

        Transaction savedTransaction = tRepository.save(transaction);
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(savedTransaction.getDescription()).isEqualTo("Ate shawarma");
        assertThat(savedTransaction.getTransactionType()).isEqualTo(EXPENSE);
        assertThat(savedTransaction.getCategory().getId()).isEqualTo(savedCategory.getId());
        assertThat(savedTransaction.getUser().getId()).isEqualTo(user.getId());
    }
}
