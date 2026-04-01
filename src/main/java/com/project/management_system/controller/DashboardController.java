package com.project.management_system.controller;

import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        return ResponseEntity.ok(dashboardService.getStatistics());
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getCustomers() {
        return ResponseEntity.ok(dashboardService.getCustomers());
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getRecentOrders() {
        return ResponseEntity.ok(dashboardService.getRecentOrders());
    }

    @GetMapping("/products/top")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getTopProducts() {
        return ResponseEntity.ok(dashboardService.getTopProducts());
    }

    // Sửa: Thêm param days và trả về cấu trúc phù hợp với frontend
    @GetMapping("/sales/chart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalesChart(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(dashboardService.getSalesChart(days));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.deleteCustomer(id));
    }
}