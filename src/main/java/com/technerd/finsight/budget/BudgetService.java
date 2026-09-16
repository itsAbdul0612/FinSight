package com.technerd.finsight.budget;

import com.technerd.finsight.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget findByCategory(String category) {
        return budgetRepository.findByCategory(category);
    }
}
