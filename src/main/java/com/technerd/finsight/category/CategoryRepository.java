package com.technerd.finsight.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameAndUserId(String name,  Long userId);

    Category findByIdAndUserId(Long id, Long userId);
}
