package com.technerd.finsight.analytics;

import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.SixMonthsTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.budget.dto.BudgetResponse;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@RequestMapping("/analytics")
@RequiredArgsConstructor
@RestController
@Tag(name = "Analytics", description = "Account-scoped spending summaries, trends, top expenses, and monthly budgets.")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService  analyticsService;
    private final BudgetService  budgetService;
    private ZoneId zoneId = ZoneId.of("Asia/Kolkata");

    @GetMapping("/summary")
    @Operation(summary = "Get a monthly total", description = "Returns the authenticated user's total for the selected transaction type and month. Defaults to expenses for the current month in Asia/Kolkata.")
    public ResponseEntity<BigDecimal> monthlySummary(
            @Parameter(description = "Transaction type to total; defaults to EXPENSE", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Month in ISO year-month format (yyyy-MM); defaults to the current month", example = "2026-11")
            @RequestParam(required = false) String month) {

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now(zoneId).toString();

        BigDecimal sumOfTheMonth = analyticsService.monthlySummary(userId, type, month);
        return ResponseEntity.ok(sumOfTheMonth);
    }

    @GetMapping("/category")
    @Operation(summary = "Get monthly spending by category", description = "Returns category totals for the authenticated user and selected month. Defaults to EXPENSE and the current month.")
    public ResponseEntity<List<CategorySpendDto>> totalMonthlySpendingOfACategory(
            @Parameter(description = "Transaction type to summarize; defaults to EXPENSE", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Month in ISO year-month format (yyyy-MM); defaults to the current month", example = "2026-11")
            @RequestParam(required = false) String month){

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now(zoneId).toString();

        List<CategorySpendDto> categoryWiseSpending = analyticsService.totalMonthlySpendingOfACategory(userId, type, month);
        return ResponseEntity.ok(categoryWiseSpending);
    }

    @GetMapping("/trend")
    @Operation(summary = "Get a six-month trend", description = "Returns monthly totals for the last six months for the authenticated user. Defaults to EXPENSE.")
    public ResponseEntity<List<SixMonthsTrendDto>> sixMonthTrend(
            @Parameter(description = "Transaction type to summarize; defaults to EXPENSE", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type) {

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;

        List<SixMonthsTrendDto> sixMonthsTrend = analyticsService.sixMonthTrend(userId, type);
        return ResponseEntity.ok(sixMonthsTrend);
    }

    @GetMapping("/top-expenses")
    @Operation(summary = "Get top expenses", description = "Returns the highest category expenses for a month. Defaults to the current month, EXPENSE, and a limit of five.")
    public ResponseEntity<List<TopExpenseOfAMonth>>  topExpensesOfAMonths(
            @Parameter(description = "Month in ISO year-month format (yyyy-MM); defaults to the current month", example = "2026-11")
            @RequestParam(required = false) String month,
            @Parameter(description = "Transaction type to rank; defaults to EXPENSE", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Maximum number of results to return", example = "5")
            @RequestParam(required = false, defaultValue = "5") Integer limit){

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now(zoneId).toString();

        List<TopExpenseOfAMonth> topExpenseOfAMonths = analyticsService.topExpensesOfAMonths(userId, month, type, limit);
        return ResponseEntity.ok(topExpenseOfAMonths);
    }

    @GetMapping("/get/all-budget")
    @Operation(summary = "Get monthly budgets", description = "Returns the authenticated user's budgets for the specified month or the current month.")
    public ResponseEntity<List<BudgetResponse>> allBudgets(
            @Parameter(description = "Month in ISO year-month format (yyyy-MM); defaults to the current month", example = "2026-11")
            @RequestParam(required = false) String month
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (month == null) month = YearMonth.now(zoneId).toString();

        List<BudgetResponse> budgetResponseList = budgetService.findAllByUserIdAndMonth(userId, month);
        return ResponseEntity.ok(budgetResponseList);
    }

}
