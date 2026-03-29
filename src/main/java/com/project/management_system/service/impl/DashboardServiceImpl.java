package com.project.management_system.service.impl;

import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CustomerRepository;
import com.project.management_system.repository.OrderRepository;
import com.project.management_system.repository.ProductRepository;
import com.project.management_system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.model.Customer;
import com.project.management_system.model.Order;
import com.project.management_system.model.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalProducts", productRepository.count());

        Double totalRevenue = orderRepository.getTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        return ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("Dashboard statistics")
                .data(stats)
                .build();
    }

    @Override
    public ApiResponse<List<CustomerResponseDTO>> getCustomers() {
        List<CustomerResponseDTO> customers = customerRepository.findAll().stream()
                .map(this::mapToCustomerDTO)
                .collect(Collectors.toList());

        return ApiResponse.<List<CustomerResponseDTO>>builder()
                .status(200)
                .message("Customers list")
                .data(customers)
                .build();
    }

    @Override
    public ApiResponse<List<OrderResponseDTO>> getRecentOrders() {
        List<OrderResponseDTO> recentOrders = orderRepository.findTop10ByOrderByOrderDateDesc()
                .stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());

        return ApiResponse.<List<OrderResponseDTO>>builder()
                .status(200)
                .message("Recent orders")
                .data(recentOrders)
                .build();
    }

    @Override
    public ApiResponse<List<ProductResponseDTO>> getTopProducts() {
        List<ProductResponseDTO> topProducts = productRepository.findTop5ByOrderByQuantityDesc()
                .stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());

        return ApiResponse.<List<ProductResponseDTO>>builder()
                .status(200)
                .message("Top products by stock")
                .data(topProducts)
                .build();
    }

    @Override
    public ApiResponse<Map<String, Object>> getSalesChart() {
        Map<String, Object> chartData = new HashMap<>();

        List<Object[]> monthlySales = orderRepository.getMonthlySales();

        List<String> months = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        for (Object[] row : monthlySales) {
            months.add("Tháng " + row[0].toString());
            revenues.add(((Number) row[1]).doubleValue());
        }

        chartData.put("months", months);
        chartData.put("revenues", revenues);

        return ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("Sales chart data")
                .data(chartData)
                .build();
    }

    @Override
    public ApiResponse<Void> deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Customer deleted successfully")
                .build();
    }

    // ========== MAPPING METHODS ==========

    private CustomerResponseDTO mapToCustomerDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setCode(customer.getCode());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setTaxCode(customer.getTaxCode());

        if (customer.getCustomerType() != null) {
            dto.setCustomerTypeName(customer.getCustomerType().getName());
        }

        return dto;
    }

    private OrderResponseDTO mapToOrderDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setOrderCode(order.getOrderCode());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());

        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethod(order.getPaymentMethod().getName());
        }

        if (order.getCustomer() != null) {
            dto.setCustomerName(order.getCustomer().getName());
        }

        if (order.getCreatedBy() != null) {
            dto.setCreatedBy(order.getCreatedBy().getFullName());
        }

        return dto;
    }

    private ProductResponseDTO mapToProductDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSalePrice(product.getSalePrice());
        dto.setQuantity(product.getQuantity());
        dto.setStatus(product.getStatus());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getUnit() != null) {
            dto.setUnitName(product.getUnit().getName());
        }

        return dto;
    }
}