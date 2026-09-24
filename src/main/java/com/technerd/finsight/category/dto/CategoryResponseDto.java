package com.technerd.finsight.category.dto;

import com.technerd.finsight.transaction.enums.TransactionType;
import lombok.Data;

@Data
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;
    private TransactionType transactionType;
    private String icon;
}
