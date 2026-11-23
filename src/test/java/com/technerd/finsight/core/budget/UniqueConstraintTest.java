package com.technerd.finsight.core.budget;

import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryRepository;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.budget.dto.BudgetUpdateDto;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueConstraintTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private CategoryRepository categoryRepository;

    @Test
    void findByUserAndCategoryAndMonthDelegatesToRepository() {
        Budget budget = Budget.builder().month("2026-08").allocatedAmount(BigDecimal.valueOf(200)).build();
        when(budgetRepository.findByUser_IdAndCategory_IdAndMonth(1L, 1L, "2026-08"))
                .thenReturn(budget);

        BudgetService service = new BudgetService(budgetRepository, modelMapper);

        assertThat(service.findByUserAndCategoryAndMonth(1L, 1L, "2026-08")).isSameAs(budget);
    }

    @Test
    void findAllByUserAndMonthMapsBudgets() {
        Budget budget = Budget.builder().month("2026-08").allocatedAmount(BigDecimal.valueOf(200)).build();
        BudgetResponse response = new BudgetResponse();
        when(budgetRepository.findAllByUserIdAndMonth(1L, "2026-08")).thenReturn(List.of(budget));
        when(modelMapper.map(budget, BudgetResponse.class)).thenReturn(response);

        BudgetService service = new BudgetService(budgetRepository, modelMapper);

        assertThat(service.findAllByUserIdAndMonth(1L, "2026-08")).containsExactly(response);
    }

    @Test
    void updateAllocatedAmountUpdatesExistingBudget() {
        User user = User.builder().id(1L).role(Role.USER).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        Budget budget = Budget.builder()
                .allocatedAmount(BigDecimal.valueOf(100))
                .spentAmount(BigDecimal.valueOf(50))
                .isBreached(false)
                .build();
        when(budgetRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(budget)).thenReturn(budget);
        when(modelMapper.map(budget, BudgetResponse.class)).thenReturn(new BudgetResponse());
        BudgetUpdateDto update = new BudgetUpdateDto();
        update.setAllocatedAmount(BigDecimal.valueOf(200));

        BudgetService service = new BudgetService(budgetRepository, modelMapper);
        BudgetResponse result = service.updateAllocatedAmount(update, 10L);

        assertThat(result).isNotNull();
        assertThat(budget.getAllocatedAmount()).isEqualByComparingTo("200");
        verify(budgetRepository).save(budget);
        SecurityContextHolder.clearContext();
    }
}
