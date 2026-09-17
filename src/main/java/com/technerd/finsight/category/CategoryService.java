package com.technerd.finsight.category;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.dto.CategoryDto;
import com.technerd.finsight.security.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto, User user) {

        String name = categoryDto.getName();
        Long id = user.getId();

        Category byName = categoryRepository.findByNameAndUserId(name, id);

        if (byName != null) {
            throw new RuntimeException("Category with this name already exists");
        }

        log.info("Creating new category with name: {}", categoryDto.getName());
        Category category = Category.builder()
                .user(user)
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .description(categoryDto.getDescription())
                .build();

        categoryRepository.save(category);

        Budget budget = Budget
                .builder()
                .category(category)
                .allocatedAmount(categoryDto.getAllocatedAmount())
                .spentAmount(categoryDto.getSpentAmount())
                .month(YearMonth.now().toString())
                .isBreached(false)
                .user(user)
                .build();

         budgetService.save(budget);

         log.info("New Category created. CategoryId: {}", category.getId());
         return categoryDto;
    }

    public Category findByIdAndUserId(Long id, Long userId) {
        return categoryRepository
                .findByIdAndUserId(id, userId);
    }
}
