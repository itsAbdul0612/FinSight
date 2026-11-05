package com.technerd.finsight.core.budget.dto;

import com.technerd.finsight.core.budget.Budget;
import com.technerd.finsight.core.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "BudgetResponse", description = "A user's budget and its associated category for a particular month.")
public class BudgetResponse  {

    @Schema(description = "Budget identifier", example = "42")
    private Long id;

    @Schema(description = "Amount allocated to the category", example = "500.00")
    private BigDecimal allocatedAmount;

    @Schema(description = "Amount spent in the category during the month", example = "125.50")
    private BigDecimal spentAmount;

    @Schema(description = "Budget month in ISO year-month format (yyyy-MM)", example = "2026-11")
    private String month;

    @Schema(description = "Whether spending has exceeded the allocated amount", example = "false")
    private Boolean isBreached;

    @Schema(description = "Category associated with this budget")
    private CategoryResponse category;

    @Data
    @Schema(name = "BudgetCategoryResponse", description = "Category details included with a budget.")
    public static class CategoryResponse {
        // Can centralize this class to reduce redundancy

        @Schema(description = "Category identifier", example = "7")
        private Long id;
        @Schema(description = "Category icon identifier", example = "restaurant")
        private String icon;
        @Schema(description = "Category name", example = "Food")
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
