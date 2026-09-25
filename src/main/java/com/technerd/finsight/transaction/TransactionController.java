package com.technerd.finsight.transaction;

import com.technerd.finsight.category.Category;
import com.technerd.finsight.security.entity.User;
import com.technerd.finsight.security.service.UserService;
import com.technerd.finsight.transaction.dto.TransactionDto;
import com.technerd.finsight.transaction.dto.TransactionResponse;
import com.technerd.finsight.transaction.enums.TransactionType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequestMapping("/transaction")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService  userService;
    private final ModelMapper modelMapper;

    // Create a transaction.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/create/{categoryId}")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionDto transactionDto,
                                                                 @PathVariable Long categoryId) {

        User userEntity = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = userEntity.getId();
        User user = userService.findById(userId);

        transactionDto.setCategoryId(categoryId);
        Transaction transaction = transactionService.createTransaction(transactionDto, user);
        Category category = transaction.getCategory();

        TransactionResponse.CategoryResponse categoryResponse = modelMapper
                .map(category, TransactionResponse.CategoryResponse.class);

        TransactionResponse transactionResponse = modelMapper.
                map(transaction, TransactionResponse.class);

        transactionResponse.setCategory(categoryResponse);

        return ResponseEntity.ok(transactionResponse);
    }
    // -----------------------------------------------------------------------------------------------

    // Get by id.
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        Transaction transaction = transactionService.getTransactionById(userId, id);
        if (transaction == null) {
            throw new EntityNotFoundException("Transaction with id " + id + " not found");
        }

        TransactionResponse transactionResponse = modelMapper.map(transaction, TransactionResponse.class);
        return ResponseEntity.ok(transactionResponse);
    }
    // -----------------------------------------------------------------------------------------------

    // Get all transactions sorted and paginated.
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/get-all")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int  pageSize,

            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Long categoryId ,
            @RequestParam(required = false) Sort.Direction sortDirection,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) TransactionType transactionType, //
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) LocalDateTime date) {

        Sort sort = Sort.by(
                sortDirection != null ? sortDirection : Sort.Direction.DESC,
                sortBy != null ? sortBy : "transactionDate"
        );

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<TransactionResponse> allTransactions = transactionService.getAll(userId, pageable,
                categoryId, transactionType, startDate, endDate, date, minAmount, maxAmount, amount);

        return ResponseEntity.ok(allTransactions);
    }
    // -----------------------------------------------------------------------------------------------

    // Soft delete.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        transactionService.softDelete(userId, id);

        return ResponseEntity.ok().build();
    }
    // -----------------------------------------------------------------------------------------------

}