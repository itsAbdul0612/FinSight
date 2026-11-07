package com.technerd.finsight.core.budget;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


    Budget findByUser_IdAndCategory_IdAndMonth(Long userId, Long categoryId, String month);

    @EntityGraph(attributePaths = "category")
    @Query("select b from Budget b where b.user.id = :userId and b.month = :month")
    List<Budget> findAllByUserIdAndMonth(Long userId, String month);

    Optional<Budget> findByIdAndUser_Id(Long id, Long userId);

    void deleteByCategory_IdAndUser_Id(Long id, Long userId);

}