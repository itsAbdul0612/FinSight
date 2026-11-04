package com.technerd.finsight.core.budget;

import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.budget.dto.BudgetUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ModelMapper modelMapper;

    @CacheEvict(cacheNames = "all-budget", key = "#userId")
    public Budget save(Budget budget, Long userId) {
        return budgetRepository.save(budget);
    }

    public Budget findByCategoryId(long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public Budget findByUserAndCategoryAndMonth(Long userId, Long categoryId, String month) {
        return budgetRepository.findByUser_IdAndCategory_IdAndMonth(userId, categoryId, month);
    }

    @Cacheable(cacheNames = "all-budget", key = "#userId")
    public List<BudgetResponse> findAllByUserIdAndMonth(Long userId, String month) {
        List<Budget> byUserIdAndMonth = budgetRepository.findAllByUserIdAndMonth(userId, month);
        List<BudgetResponse> budgetResponseList = byUserIdAndMonth
                .stream()
                .map(element -> modelMapper.map(element, BudgetResponse.class))
                .toList();

        return budgetResponseList;
    }

    @CacheEvict(cacheNames = "all-budget", key = "#userId")
    public void deleteByCategoryIdAndUserId(Long id, Long userId) {
        budgetRepository.deleteByCategory_IdAndUser_Id(id, userId);
    }

//    @CacheEvict(cacheNames = "all-budget", key = "#userId")
    public BudgetResponse updateAllocatedAmount(BudgetUpdateDto newBudget, Long id) {

        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Budget not found"));

        if (newBudget.getAllocatedAmount() != null) {
            existingBudget.setAllocatedAmount(newBudget.getAllocatedAmount());
        }
        if (existingBudget.getSpentAmount().compareTo(existingBudget.getAllocatedAmount()) < 0) {
            existingBudget.setIsBreached(false);
        }

        Budget updatedBudget = budgetRepository.save(existingBudget);

       return modelMapper.map(updatedBudget, BudgetResponse.class);
    }



}
