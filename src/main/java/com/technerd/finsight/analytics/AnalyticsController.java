package com.technerd.finsight.analytics;

import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.MonthTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import com.technerd.finsight.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RequestMapping("/analytics")
@RequiredArgsConstructor
@RestController
public class AnalyticsController {
    private final AnalyticsService  analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<BigDecimal> monthlySummary(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String month) {

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now().toString();

        BigDecimal sumOfTheMonth = analyticsService.monthlySummary(userId, type, month);
        return ResponseEntity.ok(sumOfTheMonth);
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategorySpendDto>> totalMonthlySpendingOfACategory(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String month){

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now().toString();

        List<CategorySpendDto> categoryWiseSpending = analyticsService.totalMonthlySpendingOfACategory(userId, type, month);
        return ResponseEntity.ok(categoryWiseSpending);
    }

    @GetMapping("/trend")
    public ResponseEntity<List<MonthTrendDto>> sixMonthTrend(@RequestParam(required = false) TransactionType type){

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;

        List<MonthTrendDto> sixMonthsTrend = analyticsService.sixMonthTrend(userId, type);
        return ResponseEntity.ok(sixMonthsTrend);
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TopExpenseOfAMonth>>  topExpensesOfAMonths(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Integer limit){

        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (type == null) type = TransactionType.EXPENSE;
        if (month == null) month = YearMonth.now().toString();
        if (limit == null) limit = 5;

        List<TopExpenseOfAMonth> topExpenseOfAMonths = analyticsService.topExpensesOfAMonths(userId, month, type, limit);
        return ResponseEntity.ok(topExpenseOfAMonths);
    }

}
