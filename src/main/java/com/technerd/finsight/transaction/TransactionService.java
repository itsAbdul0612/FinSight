package com.technerd.finsight.transaction;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.Category;
import com.technerd.finsight.category.CategoryService;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.transaction.dto.TransactionDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto, User user) {

        Long categoryId = transactionDto.getCategoryId();
        Category category = categoryService.findById(categoryId);

        if (category == null) {
            throw new NoSuchElementException("Category not found");
        }

        Transaction newTransaction = Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionDate(LocalDateTime.now())
                .user(user)
                .transactionType(transactionDto.getTransactionType())
                .description(transactionDto.getDescription())
                .category(category)
                .build();

        Budget budget = budgetService.findByUserAndCategoryAndMonth(
                user.getId(), transactionDto.getCategoryId(), YearMonth.now().toString()
        );
        budget.setSpentAmount(
                budget.getSpentAmount().add(transactionDto.getAmount())
        );
        budgetService.save(budget);

        return transactionRepository.save(newTransaction);
    }
}
