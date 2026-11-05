package com.technerd.finsight.core.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "TransactionRequest", description = "Transaction details submitted when creating a transaction.")
public class TransactionDto {

    @Schema(description = "Optional description of the transaction", example = "Lunch")
    private String description;
    @Schema(description = "Transaction amount", example = "24.95")
    private BigDecimal amount;
    @Schema(description = "Category identifier; the create endpoint also accepts the category ID in its path", example = "7")
    private Long categoryId;
}
