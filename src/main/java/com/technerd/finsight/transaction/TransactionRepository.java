package com.technerd.finsight.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
                                               JpaSpecificationExecutor<Transaction> {

    // Find all transactions of a user.
    Page<Transaction> findAllByUserId(Pageable pageable, Long userId);

    // Find a single transaction of a user.
    Transaction findByUserIdAndId(Long userId, Long id);

}