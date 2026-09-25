package com.technerd.finsight.category.exampleseed;

import com.technerd.finsight.transaction.enums.TransactionType;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.technerd.finsight.transaction.enums.TransactionType.EXPENSE;

// Example category to seed on sign-up

@Component
@Data
public class ExampleCategory {
    private Long id;
    private String name = "Food Budget (example)";
    private String description = "Monthly food budget";
    private TransactionType transactionType = EXPENSE;
    private String icon = "\uD83C\uDF72";

    // Budget

    private BigDecimal allocatedAmount = BigDecimal.valueOf(8000.00);
    private BigDecimal spentAmount =  BigDecimal.ZERO;
}
