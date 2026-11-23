package com.technerd.finsight.core;

import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryRepository;
import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreFlowTest {

    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private com.technerd.finsight.core.budget.BudgetService budgetService;
    @Mock private ModelMapper modelMapper;

    @Test
    void createCategoryAndBudgetAreConsistent() {
        CategoryService service = new CategoryService(userRepository, categoryRepository, budgetService, modelMapper);
        User user = User.builder().id(1L).name("Core User").build();
        CategoryCreateDto dto = new CategoryCreateDto();
        dto.setName("Food");
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setAllocatedAmount(new BigDecimal("500"));
        dto.setSpentAmount(BigDecimal.ZERO);

        when(categoryRepository.findByNameIgnoreCaseAndUserId("Food", 1L)).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(10L);
            return category;
        });

        CategoryCreateDto result = service.createCategory(dto, user);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Food");
    }
}
