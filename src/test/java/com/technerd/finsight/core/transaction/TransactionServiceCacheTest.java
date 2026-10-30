package com.technerd.finsight.core.transaction;

import com.technerd.finsight.core.budget.BudgetService;
import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.core.category.CategoryService;
import com.technerd.finsight.core.transaction.dto.TransactionDto;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.user.User;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class TransactionServiceCacheTest {

    @Test
    void createTransactionEvictsAllMonthlySummaryCacheEntries() {
        try (var context = new AnnotationConfigApplicationContext(CacheTestConfiguration.class)) {
            TransactionService transactionService = context.getBean(TransactionService.class);
            TransactionRepository transactionRepository = context.getBean(TransactionRepository.class);
            CategoryService categoryService = context.getBean(CategoryService.class);
            Cache summaryCache = Objects.requireNonNull(
                    context.getBean(CacheManager.class).getCache("monthly_summary"));

            User user = User.builder()
                    .id(1L)
                    .totalBalance(new BigDecimal("100.00"))
                    .build();
            Category category = Category.builder()
                    .id(2L)
                    .transactionType(TransactionType.INCOME)
                    .build();
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setCategoryId(category.getId());
            transactionDto.setAmount(new BigDecimal("25.00"));
            transactionDto.setDescription("Paycheck");

            given(categoryService.findByIdAndUserId(category.getId(), user.getId())).willReturn(category);
            given(transactionRepository.save(any(Transaction.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));
            summaryCache.put("user-1", "cached summary");
            summaryCache.put("user-2", "another cached summary");

            transactionService.createTransaction(transactionDto, user);

            assertThat(summaryCache.get("user-1")).isNull();
            assertThat(summaryCache.get("user-2")).isNull();
        }
    }

    @Configuration
    @EnableCaching
    static class CacheTestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("monthly_summary");
        }

        @Bean
        TransactionService transactionService(
                TransactionRepository transactionRepository,
                CategoryService categoryService,
                BudgetService budgetService,
                UserSecurityService userSecurityService,
                ApplicationEventPublisher eventPublisher,
                ModelMapper modelMapper) {
            return new TransactionService(
                    transactionRepository,
                    categoryService,
                    budgetService,
                    userSecurityService,
                    eventPublisher,
                    modelMapper);
        }

        @Bean
        TransactionRepository transactionRepository() {
            return mock(TransactionRepository.class);
        }

        @Bean
        CategoryService categoryService() {
            return mock(CategoryService.class);
        }

        @Bean
        BudgetService budgetService() {
            return mock(BudgetService.class);
        }

        @Bean
        UserSecurityService userSecurityService() {
            return mock(UserSecurityService.class);
        }

        @Bean
        ApplicationEventPublisher eventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }

        @Bean
        ModelMapper modelMapper() {
            return mock(ModelMapper.class);
        }
    }
}
