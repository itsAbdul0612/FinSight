package com.technerd.finsight.budget;

import com.technerd.finsight.budget.dto.BudgetResponse;
import com.technerd.finsight.budget.dto.BudgetUpdateDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ModelMapper modelMapper;

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget findByCategoryId(long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public Budget findByUserAndCategoryAndMonth(Long userId, Long categoryId, String month) {
        return budgetRepository.findByUser_IdAndCategory_IdAndMonth(userId, categoryId, month);
    }

    public Page<BudgetResponse> findAllByUserIdAndMonth(Long userId, Pageable pageable, String month) {
        Page<Budget> byUserIdAndMonth = budgetRepository.findByUser_IdAndMonth(userId, month, pageable);
        return byUserIdAndMonth.map(BudgetResponse::from);
    }

    public void deleteByCategoryIdAndUserId(Long id, Long userId) {
        budgetRepository.deleteByCategory_IdAndUser_Id(id, userId);
    }

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
