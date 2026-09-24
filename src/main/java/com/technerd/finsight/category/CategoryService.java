package com.technerd.finsight.category;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.dto.CategoryCreateDto;
import com.technerd.finsight.category.dto.CategoryResponseDto;
import com.technerd.finsight.security.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

// Create.
    // -----------------------------------------------------------------------------------------------

    @Transactional
    public CategoryCreateDto createCategory(CategoryCreateDto categoryDto, User user) {

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
                .transactionType(categoryDto.getTransactionType())
                .description(categoryDto.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);

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

         categoryDto.setId(savedCategory.getId());
         return categoryDto;
    }

    public Category findByIdAndUserId(Long id, Long userId) {
        return categoryRepository
                .findByIdAndUserId(id, userId);
    }
    // -----------------------------------------------------------------------------------------------


    // Get All.
    // -----------------------------------------------------------------------------------------------
    public List<CategoryResponseDto> findAllByUserId(Long userId) {

        List<Category> categoryList = categoryRepository.findAllByUserId(userId);

        log.info("Found {} Categories.", categoryList.size());
        return categoryList
                .stream()
                .map(category -> modelMapper.map(category, CategoryResponseDto.class))
                .toList();
    }
    // -----------------------------------------------------------------------------------------------

    // Patch update a category by id.
    // -----------------------------------------------------------------------------------------------
    public Category updateById(Long id, Category category){
        log.info("Trying to updating category with id: {}", id);
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory == null) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }

        if (category.getName() != null) existingCategory.setName(category.getName());
        if (category.getIcon() != null) existingCategory.setIcon(category.getIcon());
        if (category.getDescription() != null) existingCategory.setDescription(category.getDescription());
        if (category.getTransactionType() != null) existingCategory.setTransactionType(category.getTransactionType());

        log.info("Category with id: {} updated", id);
        return categoryRepository.save(existingCategory);
    }
    // -----------------------------------------------------------------------------------------------

    // Delete a category by id.
    // -----------------------------------------------------------------------------------------------
    @Transactional
    public void deleteById(Long id){
        log.info("Trying to delete category with id: {}", id);
        if (categoryRepository.existsById(id)) {
            budgetService.deleteByCategoryId(id);
            categoryRepository.deleteById(id);
        }
        log.info("Category with id: {} was not found", id);
        throw new EntityNotFoundException("Category with id " + id + " not found");
    }
    // -----------------------------------------------------------------------------------------------


}
