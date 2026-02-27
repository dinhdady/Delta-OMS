package com.project.management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO {

    private String sku;
    private String name;
    private String description;
    private BigDecimal importPrice;
    private BigDecimal salePrice;
    private int quantity;
    private String status;
    private Long categoryId;
    private Long unitId;
}

