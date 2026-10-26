package com.technerd.finsight.core.budget;

import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryRepository;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class UniqueConstraintTest {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should find budget by user id, category id, and month")
    void testFindByUserAndCategoryAndMonth() {
        Budget budget = budgetService.findByUserAndCategoryAndMonth(1L, 1L, "2026-08");

        assertThat(budget).isNotNull();
        assertThat(budget.getUser().getId()).isEqualTo(1L);
        assertThat(budget.getCategory().getId()).isEqualTo(1L);
        assertThat(budget.getMonth()).isEqualTo("2026-08");
        assertThat(budget.getAllocatedAmount()).isNotNull().isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return null when budget does not exist for specified month")
    void testFindByUserAndCategoryAndMonth_NotFound() {
        Budget budget = budgetService.findByUserAndCategoryAndMonth(1L, 1L, "1999-01");

        assertThat(budget).isNull();
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException on duplicate (user, category, month)")
    void testUniqueConstraintViolation() {
        User user = userRepository.findById(1L).orElseThrow();
        Category category = categoryRepository.findById(1L).orElseThrow();

        Budget duplicateBudget = Budget.builder()
                .user(user)
                .category(category)
                .month("2026-08")
                .allocatedAmount(BigDecimal.valueOf(10000))
                .spentAmount(BigDecimal.ZERO)
                .isBreached(false)
                .build();

        assertThatThrownBy(() -> budgetRepository.saveAndFlush(duplicateBudget))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
