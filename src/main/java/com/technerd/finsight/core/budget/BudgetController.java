package com.technerd.finsight.core.budget;

import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.budget.dto.BudgetUpdateDto;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RequestMapping("/budget")
@RequiredArgsConstructor
@RestController
public class BudgetController {

    private final BudgetService budgetService;

    // Get all budget (default: current month) or pass a custom month.
    // Specifications maybe added later for filters like date range, etc.
    @GetMapping("/get")
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @RequestParam(required = false) String customMonth
    ) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        String month = customMonth != null ? customMonth : YearMonth.now().toString();
        List<BudgetResponse> allByUserIdAndMonth = budgetService.findAllByUserIdAndMonth(userId,  month);

        return ResponseEntity.ok(allByUserIdAndMonth);
    }

    @PutMapping("/update/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@RequestBody BudgetUpdateDto incomingBudget,
                                                       @PathVariable Long budgetId) {

        BudgetResponse budget = budgetService.updateAllocatedAmount(incomingBudget, budgetId);
        return ResponseEntity.ok(budget);
    }


}
