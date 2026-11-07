package com.technerd.finsight.core.category;

import com.technerd.finsight.core.transaction.Transaction;
import com.technerd.finsight.core.transaction.TransactionRepository;
import com.technerd.finsight.core.transaction.TransactionService;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Should find all categories by user id")
    public void testFindAllCategoriesByUserId() {
        Long userId = 1L;
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);

        assertThat(categoryList)
                .as("Category list for user should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        assertThat(categoryList).allSatisfy(category -> {
            assertThat(category.getId()).isNotNull();
            assertThat(category.getName()).isNotBlank();
            assertThat(category.getUser()).isNotNull();
            assertThat(category.getUser().getId()).isEqualTo(userId);
        });
    }

    @Test
    @DisplayName("Should find category by name and user id")
    public void testFindCategoryByNameAndUserId() {
        Long userId = 1L;
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);
        assertThat(categoryList).isNotEmpty();

        Category expectedCategory = categoryList.get(0);
        Category foundCategory = categoryRepository.findByNameIgnoreCaseAndUserId(expectedCategory.getName(), userId);

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(expectedCategory.getId());
        assertThat(foundCategory.getName()).isEqualTo(expectedCategory.getName());
    }

    @Test
    @DisplayName("Should find category by id and user id")
    public void testFindCategoryByIdAndUserId() {
        Long userId = 1L;
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);
        assertThat(categoryList).isNotEmpty();

        Category expectedCategory = categoryList.get(0);
        Category foundCategory = categoryRepository.findByIdAndUserId(expectedCategory.getId(), userId).orElse(null);

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(expectedCategory.getId());
        assertThat(foundCategory.getName()).isEqualTo(expectedCategory.getName());
    }

    @Test
    @DisplayName("Should fetch paginated transactions for user")
    public void testFindPaginatedTransactions() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        Page<TransactionResponse> page = transactionService.getAll(
                userId, pageable, null, null, null, null, null, null, null, null
        );

        assertThat(page).isNotNull();
        assertThat(page.getContent())
                .as("Transaction content should not be null or empty")
                .isNotNull()
                .isNotEmpty();
        assertThat(page.getContent()).hasSizeLessThanOrEqualTo(5);

        assertThat(page.getContent()).allSatisfy(transactionResponse -> {
            assertThat(transactionResponse.getId()).isNotNull();
            assertThat(transactionResponse.getAmount()).isNotNull().isGreaterThan(BigDecimal.ZERO);
            assertThat(transactionResponse.getTransactionType()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should find transaction by user id and transaction id")
    public void testFindTransactionByUserIdAndId() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());

        Page<TransactionResponse> page = transactionService.getAll(
                userId, pageable, null, null, null, null, null, null, null, null
        );
        assertThat(page.getContent()).isNotEmpty();

        Long transactionId = page.getContent().get(0).getId();
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, transactionId);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(transactionId);
        assertThat(transaction.getUser().getId()).isEqualTo(userId);
        assertThat(transaction.getIsDeleted()).isFalse();
    }
}
