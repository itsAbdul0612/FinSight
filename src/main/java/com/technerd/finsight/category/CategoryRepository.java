package com.technerd.finsight.category;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameAndUserId(String name,  Long userId);

    Category findByIdAndUserId(Long id, Long userId);

    List<Category> findAllByUserId(Long userId);
}
