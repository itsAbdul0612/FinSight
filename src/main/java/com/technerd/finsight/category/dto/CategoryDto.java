package com.technerd.finsight.category.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class CategoryDto {

    private String name;
    private String description;
    private String icon;

    // Budget
    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount =  BigDecimal.ZERO;

}
