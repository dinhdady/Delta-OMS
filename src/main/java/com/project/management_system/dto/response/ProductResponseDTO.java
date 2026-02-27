package com.project.management_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDTO {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal salePrice;
    private int quantity;
    private String status;
    private String categoryName;
    private String unitName;
}

