package com.technerd.finsight.budget;

import com.technerd.finsight.budget.dto.BudgetResponse;
import com.technerd.finsight.budget.dto.BudgetUpdateDto;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RequestMapping("/budget")
@RequiredArgsConstructor
@RestController
public class BudgetController {

    private final BudgetService budgetService;

    // Get all budget (default: current month) or pass a custom month.
    // Specifications maybe added later for filters like date range, etc.
    @GetMapping("/get")
    public ResponseEntity<Page<BudgetResponse>> getAllBudgets(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Sort.Direction sortDirection,
            @RequestParam(required = false) String customMonth) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        String month = customMonth != null ? customMonth : YearMonth.now().toString();

        Sort sort = Sort.by(
                sortDirection != null ? sortDirection : Sort.Direction.DESC,
                sortBy != null ? sortBy : "month");

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<BudgetResponse> allByUserIdAndMonth = budgetService
                .findAllByUserIdAndMonth(userId, pageable,  month);

        return ResponseEntity.ok(allByUserIdAndMonth);
    }

    @PutMapping("/update/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@RequestBody BudgetUpdateDto incomingBudget,
                                                       @PathVariable Long budgetId) {

        BudgetResponse budget = budgetService.updateAllocatedAmount(incomingBudget, budgetId);
        return ResponseEntity.ok(budget);
    }


}
