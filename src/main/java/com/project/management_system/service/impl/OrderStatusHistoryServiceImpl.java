package com.project.management_system.service.impl;

import com.project.management_system.model.OrderStatusHistory;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.OrderStatusHistoryRepository;
import com.project.management_system.service.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {

    private final OrderStatusHistoryRepository repository;

    @Override
    public ApiResponse<List<OrderStatusHistory>> getHistory(Long orderId) {

        List<OrderStatusHistory> history =
                repository.findByOrderId(orderId);

        return ApiResponse.<List<OrderStatusHistory>>builder()
                .status(200)
                .message("Order status history retrieved")
                .data(history)
                .build();
    }
}
