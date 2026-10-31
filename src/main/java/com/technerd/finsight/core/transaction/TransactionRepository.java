package com.technerd.finsight.core.transaction;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query(value = "Select * from transaction where user_id = :userId limit 10",
    nativeQuery = true)
    List<Transaction> getLastTenTransactions(Long userId);

}