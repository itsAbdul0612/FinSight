package com.technerd.finsight.core.category.dto;

import com.technerd.finsight.core.transaction.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CategoryCreateDto {

    private Long id;
    private String name;
    private String description;
    private TransactionType transactionType;
    private String icon;

    // Budget

    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount =  BigDecimal.ZERO;

}
