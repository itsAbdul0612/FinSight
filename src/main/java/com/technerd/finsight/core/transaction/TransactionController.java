package com.technerd.finsight.core.transaction;

import com.technerd.finsight.core.category.Category;
import com.technerd.finsight.user.User;
import com.technerd.finsight.security.service.UserSecurityService;
import com.technerd.finsight.core.transaction.dto.TransactionDto;
import com.technerd.finsight.core.transaction.dto.TransactionResponse;
import com.technerd.finsight.core.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transactions", description = "Create, retrieve, filter, and soft-delete the authenticated user's financial transactions.")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserSecurityService userSecurityService;
    private final ModelMapper modelMapper;

    // Create a transaction.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/create/{categoryId}")
    @Operation(summary = "Create a transaction", description = "Creates a transaction in the specified category for the authenticated user.")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionDto transactionDto,
                                                                 @Parameter(description = "ID of the category for this transaction", required = true)
                                                                 @PathVariable Long categoryId) {

        User userEntity = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = userEntity.getId();
        User user = userSecurityService.findById(userId);

        transactionDto.setCategoryId(categoryId);
        Transaction transaction = transactionService.createTransaction(transactionDto, user);
        Category category = transaction.getCategory();

        TransactionResponse.CategoryResponse categoryResponse = modelMapper.map(category, TransactionResponse.CategoryResponse.class);

        TransactionResponse transactionResponse = modelMapper.map(transaction, TransactionResponse.class);

        transactionResponse.setCategory(categoryResponse);

        return ResponseEntity.ok(transactionResponse);
    }
    // -----------------------------------------------------------------------------------------------

    // Get by id.
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Get a transaction", description = "Returns a transaction owned by the authenticated user.")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @Parameter(description = "ID of the transaction to retrieve", required = true)
            @PathVariable Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        TransactionResponse transaction = transactionService.getTransactionById(userId, id);
        if (transaction == null) {
            throw new EntityNotFoundException("Transaction with id " + id + " not found");
        }

        return ResponseEntity.ok(transaction);
    }
    // -----------------------------------------------------------------------------------------------

    // Get all transactions sorted, paginated and filtered (optional).
    // -----------------------------------------------------------------------------------------------
    @GetMapping("/get-all")
    @Operation(summary = "Search transactions", description = "Returns a page of the authenticated user's transactions with optional category, amount, type, and date filters.")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
                                                                        @Parameter(description = "Zero-based page number", example = "0")
                                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                                        @Parameter(description = "Number of transactions per page", example = "10")
                                                                        @RequestParam(defaultValue = "10") int pageSize,

                                                                        @Parameter(description = "Property to sort by; defaults to transactionDate")
                                                                        @RequestParam(required = false) String sortBy,
                                                                        @Parameter(description = "Filter by category ID")
                                                                        @RequestParam(required = false) Long categoryId,
                                                                        @Parameter(description = "Sort direction; defaults to DESC")
                                                                        @RequestParam(required = false) Sort.Direction sortDirection,
                                                                        @Parameter(description = "Minimum transaction amount")
                                                                        @RequestParam(required = false) BigDecimal minAmount,
                                                                        @Parameter(description = "Maximum transaction amount")
                                                                        @RequestParam(required = false) BigDecimal maxAmount,
                                                                        @Parameter(description = "Match an exact transaction amount")
                                                                        @RequestParam(required = false) BigDecimal amount,
                                                                        @Parameter(description = "Filter by transaction type")
                                                                        @RequestParam(required = false) TransactionType transactionType, //
                                                                        @Parameter(description = "Include transactions on or after this ISO date-time", example = "2026-11-01T00:00:00")
                                                                        @RequestParam(required = false) LocalDateTime startDate,

                                                                        @Parameter(description = "Include transactions on or before this ISO date-time", example = "2026-11-30T23:59:59")
                                                                        @RequestParam(required = false) LocalDateTime endDate,
                                                                        @Parameter(description = "Match this ISO date-time", example = "2026-11-06T12:00:00")
                                                                        @RequestParam(required = false) LocalDateTime date) {

        Sort sort = Sort.by(sortDirection != null ? sortDirection : Sort.Direction.DESC,
                sortBy != null ? sortBy : "transactionDate");

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = user.getId();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<TransactionResponse> allTransactions = transactionService.getAll(userId,
                pageable,
                categoryId,
                transactionType,
                startDate,
                endDate,
                date,
                minAmount,
                maxAmount,
                amount);

        return ResponseEntity.ok(allTransactions);
    }
    // -----------------------------------------------------------------------------------------------

    // Soft delete.
    // -----------------------------------------------------------------------------------------------
    @PostMapping("/soft-delete/{id}")
    @Operation(summary = "Soft-delete a transaction", description = "Marks a transaction owned by the authenticated user as deleted.")
    public ResponseEntity<?> softDelete(
            @Parameter(description = "ID of the transaction to delete", required = true)
            @PathVariable Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        transactionService.softDelete(userId, id);

        return ResponseEntity.ok().build();
    }
    // -----------------------------------------------------------------------------------------------

}