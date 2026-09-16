package com.technerd.finsight.transaction;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.Category;
import com.technerd.finsight.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService  budgetService;
    private final ModelMapper modelMapper;

    public Transaction createTransaction(TransactionDto transactionDto, User user) {

        Category category = modelMapper.map(transactionDto.getCategory(), Category.class);

        Transaction newTransaction = Transaction.builder()
                .amount(transactionDto.getAmount())
                .transactionDate(LocalDateTime.now())
                .user(user)
                .transactionType(transactionDto.getTransactionType())
                .description(transactionDto.getDescription())
                .category(category)
                .build();

        Budget budget = budgetService.findByCategory(transactionDto.getCategory().toString());

        budget.setSpentAmount(
                budget.getSpentAmount().add(transactionDto.getAmount())
        );
        budgetService.save(budget);
        return transactionRepository.save(newTransaction);
    }
}
