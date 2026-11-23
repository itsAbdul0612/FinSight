package com.technerd.finsight.core.category;

import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import com.technerd.finsight.user.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BudgetService budgetService;
    @Mock private ModelMapper modelMapper;

    private CategoryService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new CategoryService(userRepository, categoryRepository, budgetService, modelMapper);
        user = User.builder().id(1L).name("Test").email("test@example.com").build();
    }

    @Test
    void createCategoryRejectsDuplicateName() {
        CategoryCreateDto dto = new CategoryCreateDto();
        dto.setName("Food");
        when(categoryRepository.findByNameIgnoreCaseAndUserId("Food", 1L))
                .thenReturn(Category.builder().name("Food").build());

        assertThatThrownBy(() -> service.createCategory(dto, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void findAllByUserIdMapsEntities() {
        Category category = Category.builder().id(10L).name("Food").transactionType(TransactionType.EXPENSE).build();
        CategoryResponseDto response = new CategoryResponseDto();
        response.setId(10L);
        when(categoryRepository.findAllByUserId(1L)).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryResponseDto.class)).thenReturn(response);

        assertThat(service.findAllByUserId(1L)).containsExactly(response);
    }

    @Test
    void updateByIdUpdatesOnlyProvidedFields() {
        User principal = User.builder().id(1L).role(Role.USER).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        Category existing = Category.builder().id(10L).name("Old").icon("old").build();
        Category incoming = Category.builder().name("New").build();
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        assertThat(service.updateById(10L, incoming).getName()).isEqualTo("New");
        verify(categoryRepository).save(existing);
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateByIdRejectsUnknownCategory() {
        User principal = User.builder().id(1L).role(Role.USER).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateById(10L, new Category()))
                .isInstanceOf(EntityNotFoundException.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void softDeleteMarksCategoryInactive() {
        Category category = Category.builder().id(10L).isActive(true).build();
        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(category));

        service.softDeleteById(10L, 1L);

        assertThat(category.getIsActive()).isFalse();
        verify(budgetService).deleteByCategoryIdAndUserId(10L, 1L);
    }
}
