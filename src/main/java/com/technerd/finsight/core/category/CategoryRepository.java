package com.technerd.finsight.core.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameIgnoreCaseAndUserId(String name, Long userId);

    List<Category> findAllByNameIgnoreCaseAndUserId(String name, Long userId);

    Category findByIdAndUserId(Long id, Long userId);

    List<Category> findAllByUserId(Long userId);
}
