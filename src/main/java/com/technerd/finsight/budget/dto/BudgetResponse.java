package com.technerd.finsight.budget.dto;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.category.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetResponse {

    private Long id;

    private BigDecimal allocatedAmount;

    private BigDecimal spentAmount;

    private String month;

    private Boolean isBreached;

    private CategoryResponse category;

    @Data
    public static class CategoryResponse {
        // Can centralize this class to reduce redundancy

        private Long id;
        private String icon;
        private String name;

        public static CategoryResponse from(Category category) {
            CategoryResponse categoryResponse = new CategoryResponse();

            categoryResponse.setIcon(category.getIcon());
            categoryResponse.setName(category.getName());
            categoryResponse.setId(category.getId());

            return categoryResponse;
        }
    }

    public static BudgetResponse from(Budget budget) {

        BudgetResponse budgetResponse = new BudgetResponse();

        budgetResponse.setId(budget.getId());
        budgetResponse.setAllocatedAmount(budget.getAllocatedAmount());
        budgetResponse.setSpentAmount(budget.getSpentAmount());
        budgetResponse.setMonth(budget.getMonth());
        budgetResponse.setIsBreached(budget.getIsBreached());
        budgetResponse.setCategory(CategoryResponse.from(budget.getCategory()));

        return budgetResponse;
    }

}
