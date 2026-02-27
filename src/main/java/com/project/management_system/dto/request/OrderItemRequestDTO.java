package com.project.management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequestDTO {

    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
}

