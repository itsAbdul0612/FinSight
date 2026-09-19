package com.technerd.finsight.category.dto;

import com.technerd.finsight.transaction.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private TransactionType transactionType;
    private String icon;

    // Budget

    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount =  BigDecimal.ZERO;

}
