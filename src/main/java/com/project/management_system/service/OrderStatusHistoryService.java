package com.project.management_system.service;

import com.project.management_system.model.OrderStatusHistory;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface OrderStatusHistoryService {

    ApiResponse<List<OrderStatusHistory>> getHistory(Long orderId);
}