package com.technerd.finsight.analytics;

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
            SELECT coalesce(sum(transaction.amount), 0)
            FROM transaction
            WHERE transaction_type = :type AND  user_id = :userId AND to_char(transaction_date, 'YYYY-MM') = :month
  
            """,
            nativeQuery = true)
    BigDecimal monthlySummary(@Param("userId") Long userId,
                              @Param("type") String type,
                              @Param("month")  String month);

    @Query("""
            SELECT new com.technerd.finsight.analytics.CategorySpendDto(c.name, coalesce(sum(t.amount),0))
            FROM Transaction t JOIN t.category c
            WHERE t.user.id = :userId AND t.transactionType = :type
              AND FUNCTION('to_char', t.transactionDate, 'YYYY-MM') = :month AND t.isDeleted = false GROUP BY c.name
           
            """)
    List<CategorySpendDto> totalMonthlySpendingByMonthAndCategory(@Param("userId") Long userId,
                                                                  @Param("type") TransactionType type,
                                                                  @Param("month")  String month);

}
