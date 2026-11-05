package com.technerd.finsight.core.budget;

import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.budget.dto.BudgetUpdateDto;
import com.technerd.finsight.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Tag(
        name = "Budget",
        description = "Control your monthly budget."
)
@RequestMapping("/budget")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService budgetService;

    // Get all budget (default: current month) or pass a custom month.
    // Specifications maybe added later for filters like date range, etc.
    @GetMapping("/get")
    @Operation(summary = "Get budgets", description = "Returns the authenticated user's budgets for a month. Defaults to the current month in Asia/Kolkata.")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @Parameter(description = "Month in ISO year-month format (yyyy-MM); defaults to the current month", example = "2026-11")
            @RequestParam(required = false) String customMonth
    ) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        String month = customMonth != null ? customMonth : YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        List<BudgetResponse> allByUserIdAndMonth = budgetService.findAllByUserIdAndMonth(userId,  month);

        return ResponseEntity.ok(allByUserIdAndMonth);
    }

    @PutMapping("/update/{budgetId}")
    @Operation(summary = "Update a budget", description = "Updates the allocated amount for the specified budget.")
    public ResponseEntity<BudgetResponse> updateBudget(@RequestBody BudgetUpdateDto incomingBudget,
                                                       @Parameter(description = "ID of the budget to update", required = true)
                                                       @PathVariable Long budgetId) {

        BudgetResponse budget = budgetService.updateAllocatedAmount(incomingBudget, budgetId);
        return ResponseEntity.ok(budget);
    }


}
