package com.technerd.finsight.core.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "BudgetUpdateRequest", description = "Fields used to update an existing budget.")
public class BudgetUpdateDto {
    @Schema(description = "New amount allocated to the budget", example = "750.00")
    private BigDecimal allocatedAmount;
}
