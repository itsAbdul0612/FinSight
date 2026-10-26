package com.technerd.finsight.core.category;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetService budgetService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    private final ZoneId zoneId = ZoneId.of("Asia/Kolkata");

    @Nested
    @DisplayName("newMonthlyBudget Tests")
    class NewMonthlyBudgetTests {

        private String currentMonth;
        private String lastMonth;
        private User user1;
        private User user2;

        @BeforeEach
        void setUp() {
            currentMonth = YearMonth.now(zoneId).toString();
            lastMonth = YearMonth.now(zoneId).minusMonths(1).toString();

            user1 = User.builder()
                    .id(1L)
                    .name("User One")
                    .email("user1@example.com")
                    .isActive(true)
                    .build();

            user2 = User.builder()
                    .id(2L)
                    .name("User Two")
                    .email("user2@example.com")
                    .isActive(true)
                    .build();
        }

        @Test
        @DisplayName("Should successfully create new monthly budget for active category with last month budget")
        void shouldCreateNewMonthlyBudgetForActiveCategoryWithLastMonthBudget() {
            Category category = Category.builder()
                    .id(10L)
                    .name("Groceries")
                    .isActive(true)
                    .user(user1)
                    .transactionType(TransactionType.EXPENSE)
                    .build();

            Budget lastMonthBudget = Budget.builder()
                    .id(100L)
                    .allocatedAmount(new BigDecimal("5000.00"))
                    .spentAmount(new BigDecimal("3500.00"))
                    .isBreached(false)
                    .month(lastMonth)
                    .category(category)
                    .user(user1)
                    .build();

            given(userRepository.finaAllActiveUsers()).willReturn(List.of(user1));
            given(categoryRepository.findAllByUserId(user1.getId())).willReturn(List.of(category));
            given(budgetService.findByUserAndCategoryAndMonth(user1.getId(), category.getId(), lastMonth))
                    .willReturn(lastMonthBudget);

            categoryService.newMonthlyBudget();

            ArgumentCaptor<Budget> budgetCaptor = ArgumentCaptor.forClass(Budget.class);
            verify(budgetService).save(budgetCaptor.capture());

            Budget savedBudget = budgetCaptor.getValue();
            assertThat(savedBudget).isNotNull();
            assertThat(savedBudget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(savedBudget.getSpentAmount()).isEqualTo(ZERO);
            assertThat(savedBudget.getIsBreached()).isFalse();
            assertThat(savedBudget.getMonth()).isEqualTo(currentMonth);
            assertThat(savedBudget.getCategory()).isEqualTo(category);
            assertThat(savedBudget.getUser()).isEqualTo(user1);
        }

        @Test
        @DisplayName("Should skip inactive categories and not query or save budget")
        void shouldSkipInactiveCategories() {
            Category inactiveCategory = Category.builder()
                    .id(20L)
                    .name("Old Category")
                    .isActive(false)
                    .user(user1)
                    .build();

            given(userRepository.finaAllActiveUsers()).willReturn(List.of(user1));
            given(categoryRepository.findAllByUserId(user1.getId())).willReturn(List.of(inactiveCategory));

            categoryService.newMonthlyBudget();

            verify(budgetService, never()).findByUserAndCategoryAndMonth(anyLong(), anyLong(), anyString());
            verify(budgetService, never()).save(any(Budget.class));
        }

        @Test
        @DisplayName("Should not create budget when last month budget is null")
        void shouldNotCreateBudgetWhenLastMonthBudgetIsNull() {
            Category category = Category.builder()
                    .id(10L)
                    .name("Dining")
                    .isActive(true)
                    .user(user1)
                    .build();

            given(userRepository.finaAllActiveUsers()).willReturn(List.of(user1));
            given(categoryRepository.findAllByUserId(user1.getId())).willReturn(List.of(category));
            given(budgetService.findByUserAndCategoryAndMonth(user1.getId(), category.getId(), lastMonth))
                    .willReturn(null);

            categoryService.newMonthlyBudget();

            verify(budgetService).findByUserAndCategoryAndMonth(user1.getId(), category.getId(), lastMonth);
            verify(budgetService, never()).save(any(Budget.class));
        }

        @Test
        @DisplayName("Should do nothing when there are no active users")
        void shouldDoNothingWhenNoActiveUsers() {
            given(userRepository.finaAllActiveUsers()).willReturn(Collections.emptyList());

            categoryService.newMonthlyBudget();

            verify(userRepository).finaAllActiveUsers();
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(budgetService);
        }

        @Test
        @DisplayName("Should handle multiple users and categories correctly")
        void shouldHandleMultipleUsersAndCategoriesCorrectly() {
            // User 1 categories: one active with budget, one inactive
            Category user1ActiveCat = Category.builder()
                    .id(101L)
                    .name("Rent")
                    .isActive(true)
                    .user(user1)
                    .build();
            Category user1InactiveCat = Category.builder()
                    .id(102L)
                    .name("Travel")
                    .isActive(false)
                    .user(user1)
                    .build();

            Budget user1Budget = Budget.builder()
                    .id(201L)
                    .allocatedAmount(new BigDecimal("15000.00"))
                    .spentAmount(new BigDecimal("15000.00"))
                    .isBreached(true)
                    .month(lastMonth)
                    .category(user1ActiveCat)
                    .user(user1)
                    .build();

            // User 2 categories: one active without budget, one active with budget
            Category user2NoBudgetCat = Category.builder()
                    .id(201L)
                    .name("Savings")
                    .isActive(true)
                    .user(user2)
                    .build();
            Category user2ActiveCat = Category.builder()
                    .id(202L)
                    .name("Utilities")
                    .isActive(true)
                    .user(user2)
                    .build();

            Budget user2Budget = Budget.builder()
                    .id(202L)
                    .allocatedAmount(new BigDecimal("2000.00"))
                    .spentAmount(new BigDecimal("1800.00"))
                    .isBreached(false)
                    .month(lastMonth)
                    .category(user2ActiveCat)
                    .user(user2)
                    .build();

            given(userRepository.finaAllActiveUsers()).willReturn(List.of(user1, user2));
            given(categoryRepository.findAllByUserId(user1.getId())).willReturn(List.of(user1ActiveCat, user1InactiveCat));
            given(categoryRepository.findAllByUserId(user2.getId())).willReturn(List.of(user2NoBudgetCat, user2ActiveCat));

            given(budgetService.findByUserAndCategoryAndMonth(user1.getId(), user1ActiveCat.getId(), lastMonth))
                    .willReturn(user1Budget);
            given(budgetService.findByUserAndCategoryAndMonth(user2.getId(), user2NoBudgetCat.getId(), lastMonth))
                    .willReturn(null);
            given(budgetService.findByUserAndCategoryAndMonth(user2.getId(), user2ActiveCat.getId(), lastMonth))
                    .willReturn(user2Budget);

            categoryService.newMonthlyBudget();

            ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
            verify(budgetService, times(2)).save(captor.capture());

            List<Budget> savedBudgets = captor.getAllValues();
            assertThat(savedBudgets).hasSize(2);

            // Verify first saved budget (User 1)
            Budget savedUser1Budget = savedBudgets.get(0);
            assertThat(savedUser1Budget.getUser()).isEqualTo(user1);
            assertThat(savedUser1Budget.getCategory()).isEqualTo(user1ActiveCat);
            assertThat(savedUser1Budget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("15000.00"));
            assertThat(savedUser1Budget.getSpentAmount()).isEqualTo(ZERO);
            assertThat(savedUser1Budget.getIsBreached()).isFalse();
            assertThat(savedUser1Budget.getMonth()).isEqualTo(currentMonth);

            // Verify second saved budget (User 2)
            Budget savedUser2Budget = savedBudgets.get(1);
            assertThat(savedUser2Budget.getUser()).isEqualTo(user2);
            assertThat(savedUser2Budget.getCategory()).isEqualTo(user2ActiveCat);
            assertThat(savedUser2Budget.getAllocatedAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
            assertThat(savedUser2Budget.getSpentAmount()).isEqualTo(ZERO);
            assertThat(savedUser2Budget.getIsBreached()).isFalse();
            assertThat(savedUser2Budget.getMonth()).isEqualTo(currentMonth);

            // Verify inactive category was never queried
            verify(budgetService, never()).findByUserAndCategoryAndMonth(user1.getId(), user1InactiveCat.getId(), lastMonth);
        }

        @Test
        @DisplayName("Should handle active user with empty category list")
        void shouldHandleUserWithNoCategories() {
            given(userRepository.finaAllActiveUsers()).willReturn(List.of(user1));
            given(categoryRepository.findAllByUserId(user1.getId())).willReturn(Collections.emptyList());

            categoryService.newMonthlyBudget();

            verify(categoryRepository).findAllByUserId(user1.getId());
            verify(budgetService, never()).findByUserAndCategoryAndMonth(anyLong(), anyLong(), anyString());
            verify(budgetService, never()).save(any(Budget.class));
        }
    }
}
