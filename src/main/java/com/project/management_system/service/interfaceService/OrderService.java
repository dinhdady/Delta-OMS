package com.project.management_system.service.interfaceService;

import com.project.management_system.dto.request.OrderRequestDTO;
import com.project.management_system.dto.response.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO dto, String username);

    OrderResponseDTO getOrderByCode(String orderCode);

    List<OrderResponseDTO> getOrdersByCustomer(Long customerId);
}

