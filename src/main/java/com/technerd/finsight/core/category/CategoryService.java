package com.technerd.finsight.core.category;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.dto.CategoryCreateDto;
import com.technerd.finsight.core.category.dto.CategoryResponseDto;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.math.BigDecimal.ZERO;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

// Create.
    // -----------------------------------------------------------------------------------------------
    @Transactional
    @CacheEvict(cacheNames = "all-budget", key = "#user.id")
    public CategoryCreateDto createCategory(CategoryCreateDto categoryDto, User user) {

        String name = categoryDto.getName();
        Long id = user.getId();

        Category byName = categoryRepository.findByNameIgnoreCaseAndUserId(name, id);

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
    // -----------------------------------------------------------------------------------------------

    // Tools:
    // -----------------------------------------------------------------------------------------------
    public Category findByIdAndUserId(Long id, Long userId) {
        return categoryRepository
                .findByIdAndUserId(id, userId);
    }

    public CategoryResponseDto findByNameIgnoreCaseAndUserId(String name, Long userId) {
        if (categoryRepository.findByNameIgnoreCaseAndUserId(name, userId) != null) {
            Category category = categoryRepository.findByNameIgnoreCaseAndUserId(name, userId);
            return modelMapper.map(category, CategoryResponseDto.class);
        }
        log.error("Categories for userId {} not found", userId);
        throw new EntityNotFoundException();
    }
    // -----------------------------------------------------------------------------------------------


    // Get All.
    // -----------------------------------------------------------------------------------------------

    // This hurt me a lot.

    @Cacheable(cacheNames = "all-categories", key = "#userId")
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

    // Soft delete a category by id.
    // -----------------------------------------------------------------------------------------------
    @Transactional
    public void softDeleteById(Long id, Long userId) {

        log.info("Trying to soft-delete category with id: {}", id);
        Category existingCategory = categoryRepository.findByIdAndUserId(id, userId);

        if (existingCategory == null) {
            log.error("Category with id: {} was not found", id);
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }

            budgetService.deleteByCategoryIdAndUserId(id, userId);
            existingCategory.setIsActive(false);
            log.info("Category with id: {} is soft-deleted", id);
    }
    // -----------------------------------------------------------------------------------------------

    // Creates new budget for active categories every 1st of every month.
    // -----------------------------------------------------------------------------------------------
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Kolkata")
    public void newMonthlyBudget(){

        // I would use Spring Batch here but in this specific case, it is not needed and would be extra machinery

        log.info("Monthly budget reset started");
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        String CURRENT_MONTH = YearMonth.now(zoneId).toString();
        String LAST_MONTH = YearMonth.now(zoneId).minusMonths(1).toString();

        try {
            // All users with isActive = True.
            List<User> users = userRepository.finaAllActiveUsers();
            for (User user : users) {

                List<Category> categories = categoryRepository.findAllByUserId(user.getId());

                for (Category category : categories) {

                    if (category.getIsActive().equals(FALSE)) {
                        continue;
                    }

                    Budget lastMonthBudget = budgetService.findByUserAndCategoryAndMonth(
                            user.getId(),
                            category.getId(),
                            LAST_MONTH
                    );

                    if (lastMonthBudget != null) {

                        Budget budget = Budget.builder()
                                .allocatedAmount(lastMonthBudget.getAllocatedAmount())
                                .isBreached(false)
                                .category(lastMonthBudget.getCategory())
                                .spentAmount(ZERO)
                                .month(CURRENT_MONTH)
                                .user(lastMonthBudget.getUser())
                                .build();

                        budgetService.save(budget);
                    }

                }
            }
        } catch (Exception e) {
            log.error("Monthly budget reset failed", e);
        }
        log.info("Monthly budget reset finished");
    }
    // -----------------------------------------------------------------------------------------------

}
