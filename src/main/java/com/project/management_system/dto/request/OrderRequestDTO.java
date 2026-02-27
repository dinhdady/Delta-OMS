package com.project.management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    private Long customerId;
    private Long paymentMethodId;
    private String shippingAddress;
    private String note;

    private List<OrderItemRequestDTO> items;
}

