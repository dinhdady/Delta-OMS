package com.project.management_system.service;

import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.payload.ApiResponse;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    ApiResponse<Map<String, Object>> getStatistics();

    ApiResponse<List<CustomerResponseDTO>> getCustomers();

    ApiResponse<List<OrderResponseDTO>> getRecentOrders();

    ApiResponse<List<ProductResponseDTO>> getTopProducts();

    // Thêm param days
    ApiResponse<Map<String, Object>> getSalesChart(int days);

    ApiResponse<Void> deleteCustomer(Long id);
}