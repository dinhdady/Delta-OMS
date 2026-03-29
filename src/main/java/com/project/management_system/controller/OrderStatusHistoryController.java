package com.project.management_system.controller;

import com.project.management_system.model.OrderStatusHistory;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-status-history")
@RequiredArgsConstructor
public class OrderStatusHistoryController {

    private final OrderStatusHistoryService orderStatusHistoryService;

    @GetMapping("/{orderId}")
    public ApiResponse<List<OrderStatusHistory>> getHistory(
            @PathVariable Long orderId) {

        return orderStatusHistoryService.getHistory(orderId);
    }
}