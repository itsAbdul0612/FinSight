package com.technerd.finsight.core.category;

import com.technerd.finsight.core.transaction.enums.TransactionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByNameAndUserId_whenUserIdIgnoreCaseIsValid_thenReturnCategory() {
        Category category = new Category();
        category.setId(10230L);
        category.setName("Test Category");
        category.setTransactionType(TransactionType.EXPENSE);
        category.setDescription("Test Description");

        categoryRepository.save(category);

        Assertions.assertThat(category).isNotNull();
        System.out.println(category.getUser().getName());
    }

    @Test
    void findByIdAndUserId() {
    }

    @Test
    void findAllByUserId() {
    }
}