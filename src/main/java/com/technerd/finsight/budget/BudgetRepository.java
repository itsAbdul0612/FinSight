package com.technerd.finsight.budget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

//    @Query(" select b from Budget b where b.user.id = :userId and b.category.id = :categoryId and b.month = :month")
//    Budget findByUser_IdAndCategory_IdAndMonth(@PathParam("userId") Long userId,
//                                         @PathParam("categoryId") Long categoryId,
//                                         @PathParam("month") String month);
//

    Budget findByCategoryId(long id);

    Budget findByUser_IdAndCategory_IdAndMonth(Long userId, Long categoryId, String month);

    Page<Budget> findByUser_IdAndMonth(Long userId, String month, Pageable pageable);

    void deleteByCategory_IdAndUser_Id(Long id, Long userId);

    Budget findByIdAndUser_Id(Long id, Long userId);
}