package com.technerd.finsight.analytics;

import com.technerd.finsight.analytics.dto.CategorySpendDto;
import com.technerd.finsight.analytics.dto.MonthTrendDto;
import com.technerd.finsight.analytics.dto.TopExpenseOfAMonth;
import com.technerd.finsight.transaction.Transaction;
import com.technerd.finsight.transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            SELECT coalesce(sum(transaction.amount), 0.0)
            FROM transaction
            WHERE transaction_type = :type
            AND  user_id = :userId
            AND to_char(transaction_date, 'YYYY-MM') = :month
            """,
            nativeQuery = true)
    BigDecimal monthlySummary(@Param("userId") Long userId,
                              @Param("type") String type,
                              @Param("month")  String month);

    @Query("""
            SELECT new com.technerd.finsight.analytics.dto.CategorySpendDto(c.name, coalesce(sum(t.amount),0))
            FROM Transaction t
            JOIN t.category c
            WHERE t.user.id = :userId
            AND t.transactionType = :type
            AND FUNCTION('to_char', t.transactionDate, 'YYYY-MM') = :month
            AND t.isDeleted = false
            GROUP BY c.name
            """)
    List<CategorySpendDto> totalMonthlySpendingByMonthAndCategory(@Param("userId") Long userId,
                                                                  @Param("type") TransactionType type,
                                                                  @Param("month")  String month);

    @Query(value = """
              SELECT TO_CHAR(transaction_date, 'YYYY-MM') AS month, COALESCE(SUM(amount), 0) AS totalSpent
               FROM transaction
               WHERE user_id = :userId
               AND transaction_type = 'EXPENSE'
               AND is_deleted = false
               AND transaction_date >= NOW() - INTERVAL '6 months'
               GROUP BY TO_CHAR(transaction_date, 'YYYY-MM')
               ORDER BY 1 ASC
            """,
            nativeQuery = true)
    List<MonthTrendDto> trend(@Param("type") TransactionType type,
                              @Param("userId") Long userId);

    @Query(value = """
    SELECT c.name AS categoryName,
    COALESCE(SUM(t.amount), 0) AS totalSpent,
    TO_CHAR(t.transaction_date, 'YYYY-MM') AS month
    FROM transaction t
    JOIN category c ON t.category_id = c.id
    WHERE t.user_id = :userId
    AND TO_CHAR(t.transaction_date, 'YYYY-MM') = :month
    AND t.transaction_type = :#{#type.name()}
    AND t.is_deleted = false
    GROUP BY c.name, TO_CHAR(t.transaction_date, 'YYYY-MM')
    ORDER BY totalSpent DESC
    LIMIT :limit
    
    """,
            nativeQuery = true)
    List<TopExpenseOfAMonth> topExpensesByMonth(@Param("userId") Long userId,
                                                @Param("month") String month,
                                                @Param("type")  TransactionType type,
                                                @Param("limit") Integer limit);

}

