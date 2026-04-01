package com.project.management_system.dto.response;

import com.project.management_system.model.Product;
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
    private Product.ProductStatus status;
    private String categoryName;
    private String unitName;
}

