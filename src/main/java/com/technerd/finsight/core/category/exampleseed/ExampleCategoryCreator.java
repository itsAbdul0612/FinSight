package com.technerd.finsight.core.category.exampleseed;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryRepository;
import com.technerd.finsight.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExampleCategoryCreator {

    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    @Transactional
    public ExampleCategory createCategory(ExampleCategory exampleCategory, User user) {

        log.info("Creating new example category with name: {}", exampleCategory.getName());
        Category category = Category.builder()
                .user(user)
                .name(exampleCategory.getName())
                .icon(exampleCategory.getIcon())
                .transactionType(exampleCategory.getTransactionType())
                .description(exampleCategory.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);

        Budget budget = Budget
                .builder()
                .category(category)
                .allocatedAmount(exampleCategory.getAllocatedAmount())
                .spentAmount(exampleCategory.getSpentAmount())
                .month(YearMonth.now().toString())
                .isBreached(false)
                .user(user)
                .build();

        budgetService.save(budget);

        log.info("New Example Category created. CategoryId: {}", category.getId());

        exampleCategory.setId(savedCategory.getId());
        return exampleCategory;
    }

}
