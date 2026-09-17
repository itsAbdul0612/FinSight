package com.technerd.finsight.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

//    @Query(" select b from Budget b where b.user.id = :userId and b.category.id = :categoryId and b.month = :month")
//    Budget findByUser_IdAndCategory_IdAndMonth(@PathParam("userId") Long userId,
//                                         @PathParam("categoryId") Long categoryId,
//                                         @PathParam("month") String month);
//

    Budget findByUser_IdAndCategory_IdAndMonth(Long userId, Long categoryId, String month);


}