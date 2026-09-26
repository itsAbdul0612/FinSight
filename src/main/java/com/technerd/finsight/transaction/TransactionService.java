package com.technerd.finsight.transaction;

import com.technerd.finsight.budget.Budget;
import com.technerd.finsight.budget.BudgetService;
import com.technerd.finsight.category.Category;
import com.technerd.finsight.category.CategoryService;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.UserService;
import com.technerd.finsight.transaction.dto.TransactionDto;
import com.technerd.finsight.transaction.dto.TransactionResponse;
import com.technerd.finsight.transaction.enums.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.NoSuchElementException;

import static com.technerd.finsight.transaction.enums.TransactionType.EXPENSE;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final UserService userService;

    // Create a transaction.
    // -----------------------------------------------------------------------------------------------
    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto, User user) {

        Long categoryId = transactionDto.getCategoryId();
        Long userId = user.getId();
        Category category = categoryService.findByIdAndUserId(categoryId, userId);

        log.info("Trying to create a new transaction with categoryId {}", categoryId);

        if (category == null) {
            throw new NoSuchElementException("Category not found");
        }

        Transaction newTransaction = Transaction.builder().amount(transactionDto.getAmount()).transactionDate(LocalDateTime.now()).user(user).transactionType(category.getTransactionType()).description(transactionDto.getDescription()).category(category).isDeleted(false).build();

        Budget budget = budgetService.findByUserAndCategoryAndMonth(user.getId(), transactionDto.getCategoryId(), YearMonth.now().toString());

        if (newTransaction.getTransactionType() == EXPENSE) {
            budget.setSpentAmount(budget.getSpentAmount().add(transactionDto.getAmount()));

            if (budget.getSpentAmount().compareTo(budget.getAllocatedAmount()) >= 0) {
                budget.setIsBreached(true);
            }
            user.setTotalBalance(user.getTotalBalance().subtract(transactionDto.getAmount()));
            budgetService.save(budget);
        } else {
            user.setTotalBalance(user.getTotalBalance().add(transactionDto.getAmount()));
        }
        userService.save(user);

        Transaction transaction = transactionRepository.save(newTransaction);

        log.info("New transaction created. TransactionId: {}", newTransaction.getId());
        return transaction;
    }

    // -----------------------------------------------------------------------------------------------


    // Get transaction by id.
    // -----------------------------------------------------------------------------------------------
    public Transaction getTransactionById(Long userId, Long id) {
        Transaction byUserIdAndId = transactionRepository.findByUserIdAndId(userId, id);
        if (byUserIdAndId == null || byUserIdAndId.getIsDeleted()) {
            throw new NoSuchElementException("Transaction does not exist or has been deleted.");
        }
        return byUserIdAndId;
    }
    // -----------------------------------------------------------------------------------------------


    //Get all transactions.
    // -----------------------------------------------------------------------------------------------
    public Page<TransactionResponse> getAll(Long userId, Pageable pageable, Long categoryId, TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime date, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal amount) {

        log.info("Fetching transactions for userId: {}", userId);

        Specification<Transaction> spec = Specification.unrestricted();

        spec = spec.and(TransactionSpecification.belongsTo(userId));
        spec = spec.and(TransactionSpecification.hasCategory(categoryId));
        spec = spec.and(TransactionSpecification.hasType(transactionType));
        spec = spec.and(TransactionSpecification.dateBetween(startDate, endDate));
        spec = spec.and(TransactionSpecification.date(date));
        spec = spec.and(TransactionSpecification.amountBetween(minAmount, maxAmount));
        spec = spec.and(TransactionSpecification.amount(amount));

        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false));

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

        log.info("Fetched transactions for userId: {}", userId);
        return transactions.map(TransactionResponse::from);
    }
    // -----------------------------------------------------------------------------------------------

    /*
     * Soft delete:
     * 1. mark a transaction as isDeleted = true.
     * 2. reverse the spent amount in budget.
     * 3. reverse the total amount in user.
     * */
    // -----------------------------------------------------------------------------------------------
    public void softDelete(Long userId, Long transactionId) {

        Transaction transactionToBeDeleted = transactionRepository.findByUserIdAndId(userId, transactionId);
        if (transactionToBeDeleted == null || transactionToBeDeleted.getIsDeleted()) {
            throw new NoSuchElementException("Transaction does not exist or has been deleted.");
        }

        Long categoryId = transactionToBeDeleted.getCategory().getId();

        Budget budget = budgetService.findByCategoryId(categoryId);
        User user = userService.findById(userId);

        if (transactionToBeDeleted.getTransactionType() == EXPENSE) {
            budget.setSpentAmount(budget.getSpentAmount().subtract(transactionToBeDeleted.getAmount()));

            if (budget.getSpentAmount().compareTo(budget.getAllocatedAmount()) < 0) {
                budget.setIsBreached(false);
            }
            budgetService.save(budget);
            user.setTotalBalance(user.getTotalBalance().add(transactionToBeDeleted.getAmount()));
            userService.save(user);
        } else {
            user.setTotalBalance(user.getTotalBalance().subtract(transactionToBeDeleted.getAmount()));
            userService.save(user);
        }

        transactionToBeDeleted.setIsDeleted(true);
        transactionRepository.save(transactionToBeDeleted);

        log.info("Transaction soft deleted. TransactionId: {}", transactionId);
    }
    // -----------------------------------------------------------------------------------------------
}
