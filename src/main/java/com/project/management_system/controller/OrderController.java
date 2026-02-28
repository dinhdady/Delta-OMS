package com.project.management_system.controller;

import com.project.management_system.dto.request.OrderRequestDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public OrderResponseDTO createOrder(
            @RequestBody OrderRequestDTO dto,
            Authentication authentication) {

        String username = authentication.getName();
        return orderService.createOrder(dto, username);
    }

    @GetMapping("/{orderCode}")
    public OrderResponseDTO getOrderByCode(
            @PathVariable String orderCode) {
        return orderService.getOrderByCode(orderCode);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponseDTO> getOrdersByCustomer(
            @PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
}
