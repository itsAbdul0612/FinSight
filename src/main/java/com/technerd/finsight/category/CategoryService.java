package com.technerd.finsight.category;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.dto.CategoryDto;
import com.technerd.finsight.security.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto, User user) {

        Category byName = categoryRepository.findByName(categoryDto.getName());
        if (byName != null) {
            throw new RuntimeException("Category with this name already exists");
        }
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
                .month(YearMonth.now().toString())
                .user(user)
                .build();

         budgetService.save(budget);

         return categoryDto;
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(null);
    }
}
