package com.technerd.finsight.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget findByUserAndCategoryAndMonth(Long userId, Long categoryId, String month) {
      return budgetRepository.findByUser_IdAndCategory_IdAndMonth(userId, categoryId, month);
    }
}
