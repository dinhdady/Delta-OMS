package com.project.management_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {

    private String orderCode;
    private LocalDateTime orderDate;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private String orderStatus;
    private String paymentStatus;

    private String customerName;
    private String createdBy;
    private String paymentMethod;

    private List<OrderItemResponseDTO> items;
}

