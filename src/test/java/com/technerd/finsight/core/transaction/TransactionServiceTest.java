package com.technerd.finsight.core.transaction;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.transaction.dto.TransactionDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryService categoryService;
    @Mock private BudgetService budgetService;
    @Mock private UserSecurityService userSecurityService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ModelMapper modelMapper;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(transactionRepository, categoryService, budgetService,
                userSecurityService, eventPublisher, modelMapper);
    }

    @Test
    void createTransactionRejectsUnknownCategory() {
        User user = User.builder().id(1L).build();
        TransactionDto dto = new TransactionDto();
        dto.setCategoryId(99L);
        dto.setAmount(BigDecimal.TEN);
        when(categoryService.findByIdAndUserId(99L, 1L)).thenReturn(null);

        assertThatThrownBy(() -> service.createTransaction(dto, user))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void createExpenseUpdatesBalanceBudgetAndRepository() {
        User user = User.builder().id(1L).name("Test").email("test@example.com")
                .totalBalance(new BigDecimal("100")).build();
        Category category = Category.builder().id(2L).name("Food")
                .transactionType(TransactionType.EXPENSE).build();
        Budget budget = Budget.builder()
                .allocatedAmount(new BigDecimal("200"))
                .spentAmount(new BigDecimal("50"))
                .isBreached(false)
                .build();
        TransactionDto dto = new TransactionDto();
        dto.setCategoryId(2L);
        dto.setAmount(new BigDecimal("25"));
        dto.setDescription("Lunch");

        when(categoryService.findByIdAndUserId(2L, 1L)).thenReturn(category);
        when(budgetService.findByUserAndCategoryAndMonth(any(), any(), any())).thenReturn(budget);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = service.createTransaction(dto, user);

        assertThat(result.getAmount()).isEqualByComparingTo("25");
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(budget.getSpentAmount()).isEqualByComparingTo("75");
        assertThat(user.getTotalBalance()).isEqualByComparingTo("75");
        verify(budgetService).save(budget, 1L);
        verify(userSecurityService).save(user);
    }

    @Test
    void getTransactionByIdRejectsMissingOrDeletedTransaction() {
        when(transactionRepository.findByUserIdAndId(1L, 10L))
                .thenReturn(Transaction.builder().id(10L).isDeleted(true).build());

        assertThatThrownBy(() -> service.getTransactionById(1L, 10L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void softDeleteExpenseReversesSpentAmountAndBalance() {
        Transaction transaction = Transaction.builder()
                .id(10L).amount(new BigDecimal("20"))
                .transactionType(TransactionType.EXPENSE)
                .isDeleted(false)
                .category(Category.builder().id(2L).build())
                .build();
        Budget budget = Budget.builder()
                .allocatedAmount(new BigDecimal("200"))
                .spentAmount(new BigDecimal("50"))
                .isBreached(true)
                .build();
        User user = User.builder().id(1L).totalBalance(new BigDecimal("80")).build();
        when(transactionRepository.findByUserIdAndId(1L, 10L)).thenReturn(transaction);
        when(budgetService.findByCategoryId(2L)).thenReturn(budget);
        when(userSecurityService.findById(1L)).thenReturn(user);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        service.softDelete(1L, 10L);

        assertThat(transaction.getIsDeleted()).isTrue();
        assertThat(budget.getSpentAmount()).isEqualByComparingTo("30");
        assertThat(budget.getIsBreached()).isFalse();
        assertThat(user.getTotalBalance()).isEqualByComparingTo("100");
        verify(transactionRepository).save(transaction);
    }
}
