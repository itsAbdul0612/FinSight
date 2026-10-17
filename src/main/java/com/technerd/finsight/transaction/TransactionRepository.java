package com.technerd.finsight.transaction;

import com.technerd.finsight.analytics.CategorySpendDto;
import com.technerd.finsight.transaction.enums.TransactionType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    // Find all transactions of a user.
    Page<Transaction> findAllByUserId(Pageable pageable, Long userId);

    // Find a single transaction of a user.
    @EntityGraph(attributePaths = {"category"})
    Transaction findByUserIdAndId(Long userId, Long id);

    @Override
    @EntityGraph(value = "Transaction.category")
    Page<Transaction> findAll(@NonNull Specification<Transaction> specs, @NonNull Pageable pageable);




    // **** Analytic related queries ****


}