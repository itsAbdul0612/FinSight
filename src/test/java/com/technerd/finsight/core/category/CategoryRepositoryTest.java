package com.technerd.finsight.core.category;

import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.user.User;
import com.technerd.finsight.user.UserRepository;
import com.technerd.finsight.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByNameIgnoreCaseAndUserId() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("category-repository@example.com")
                .password("password")
                .role(Role.USER)
                .totalBalance(BigDecimal.ZERO)
                .isActive(true)
                .build());

        Category saved = categoryRepository.save(Category.builder()
                .name("Food")
                .description("Test category")
                .transactionType(TransactionType.EXPENSE)
                .user(user)
                .build());

        assertThat(categoryRepository.findByNameIgnoreCaseAndUserId("fOoD", user.getId()))
                .extracting(Category::getId)
                .isEqualTo(saved.getId());
    }

    @Test
    void findByIdAndUserId() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("category-by-id@example.com")
                .password("password")
                .role(Role.USER)
                .totalBalance(BigDecimal.ZERO)
                .isActive(true)
                .build());
        Category saved = categoryRepository.save(Category.builder()
                .name("Transport")
                .transactionType(TransactionType.EXPENSE)
                .user(user)
                .build());

        assertThat(categoryRepository.findByIdAndUserId(saved.getId(), user.getId()))
                .hasValueSatisfying(category -> assertThat(category.getName()).isEqualTo("Transport"));
    }

    @Test
    void findAllByUserId() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("category-list@example.com")
                .password("password")
                .role(Role.USER)
                .totalBalance(BigDecimal.ZERO)
                .isActive(true)
                .build());
        categoryRepository.save(Category.builder()
                .name("Salary")
                .transactionType(TransactionType.INCOME)
                .user(user)
                .build());

        assertThat(categoryRepository.findAllByUserId(user.getId()))
                .hasSize(1)
                .first()
                .extracting(Category::getName)
                .isEqualTo("Salary");
    }
}