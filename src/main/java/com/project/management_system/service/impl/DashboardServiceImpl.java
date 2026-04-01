package com.project.management_system.service.impl;

import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.model.Product;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CustomerRepository;
import com.project.management_system.repository.OrderRepository;
import com.project.management_system.repository.ProductRepository;
import com.project.management_system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public ApiResponse<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCustomers", customerRepository.count());
            stats.put("totalOrders", orderRepository.count());
            stats.put("totalProducts", productRepository.count());

            Double totalRevenue = orderRepository.getTotalRevenue();
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

            log.info("Statistics loaded successfully");
            return ApiResponse.<Map<String, Object>>builder()
                    .status(200)
                    .message("Dashboard statistics retrieved successfully")
                    .data(stats)
                    .build();
        } catch (Exception e) {
            log.error("Error loading statistics: {}", e.getMessage());
            return ApiResponse.<Map<String, Object>>builder()
                    .status(500)
                    .message("Error loading statistics: " + e.getMessage())
                    .data(new HashMap<>())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CustomerResponseDTO>> getCustomers() {
        try {
            List<CustomerResponseDTO> customers = customerRepository.findAll().stream()
                    .map(this::mapToCustomerDTO)
                    .collect(Collectors.toList());

            log.info("Loaded {} customers", customers.size());
            return ApiResponse.<List<CustomerResponseDTO>>builder()
                    .status(200)
                    .message("Customers retrieved successfully")
                    .data(customers)
                    .build();
        } catch (Exception e) {
            log.error("Error loading customers: {}", e.getMessage());
            return ApiResponse.<List<CustomerResponseDTO>>builder()
                    .status(500)
                    .message("Error loading customers: " + e.getMessage())
                    .data(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<OrderResponseDTO>> getRecentOrders() {
        try {
            List<OrderResponseDTO> recentOrders = orderRepository.findTop10ByOrderByOrderDateDesc()
                    .stream()
                    .map(this::mapToOrderDTO)
                    .collect(Collectors.toList());

            log.info("Loaded {} recent orders", recentOrders.size());
            return ApiResponse.<List<OrderResponseDTO>>builder()
                    .status(200)
                    .message("Recent orders retrieved successfully")
                    .data(recentOrders)
                    .build();
        } catch (Exception e) {
            log.error("Error loading recent orders: {}", e.getMessage());
            return ApiResponse.<List<OrderResponseDTO>>builder()
                    .status(500)
                    .message("Error loading recent orders: " + e.getMessage())
                    .data(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<ProductResponseDTO>> getTopProducts() {
        try {
            List<ProductResponseDTO> topProducts = new ArrayList<>();

            // Thử lấy top sản phẩm bán chạy từ order items
            try {
                List<Product> sellingProducts = productRepository.findTop10SellingProducts();
                topProducts = sellingProducts.stream()
                        .map(this::mapToProductDTO)
                        .collect(Collectors.toList());
                log.info("Loaded {} top selling products", topProducts.size());
            } catch (Exception e) {
                log.warn("Could not load top selling products, falling back to top by stock: {}", e.getMessage());
                // Fallback: lấy top 10 sản phẩm theo số lượng tồn kho
                topProducts = productRepository.findTop10ByOrderByQuantityDesc()
                        .stream()
                        .map(this::mapToProductDTO)
                        .collect(Collectors.toList());
                log.info("Loaded {} top products by stock", topProducts.size());
            }

            return ApiResponse.<List<ProductResponseDTO>>builder()
                    .status(200)
                    .message("Top products retrieved successfully")
                    .data(topProducts)
                    .build();
        } catch (Exception e) {
            log.error("Error loading top products: {}", e.getMessage());
            return ApiResponse.<List<ProductResponseDTO>>builder()
                    .status(500)
                    .message("Error loading top products: " + e.getMessage())
                    .data(new ArrayList<>())
                    .build();
        }
    }


    @Override
    public ApiResponse<Map<String, Object>> getSalesChart(int days) {
        try {
            Map<String, Object> chartData = new HashMap<>();
            List<String> labels = new ArrayList<>();
            List<Double> revenues = new ArrayList<>();

            // Nếu days <= 31, hiển thị theo ngày
            if (days <= 31) {
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusDays(days - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    labels.add(date.format(formatter));
                    Double revenue = orderRepository.getTotalRevenueByDate(date);
                    revenues.add(revenue != null ? revenue : 0.0);
                }
            }
            // Nếu days > 31, hiển thị theo tháng
            else {
                // Tính ngày bắt đầu dựa trên số ngày
                LocalDate startDate = LocalDate.now().minusDays(days);
                LocalDateTime startDateTime = startDate.atStartOfDay();

                List<Object[]> monthlySales = orderRepository.getMonthlySales(startDateTime);

                if (monthlySales.isEmpty()) {
                    // Fallback: hiển thị 12 tháng gần nhất
                    for (int i = 11; i >= 0; i--) {
                        LocalDate date = LocalDate.now().minusMonths(i);
                        labels.add(String.format("Tháng %d/%d", date.getMonthValue(), date.getYear()));
                        revenues.add(0.0);
                    }
                } else {
                    for (Object[] row : monthlySales) {
                        int year = ((Number) row[0]).intValue();
                        int month = ((Number) row[1]).intValue();
                        labels.add(String.format("Tháng %d/%d", month, year));
                        revenues.add(((Number) row[2]).doubleValue());
                    }
                    // Đảo ngược để hiển thị từ cũ đến mới
                    Collections.reverse(labels);
                    Collections.reverse(revenues);
                }
            }

            chartData.put("labels", labels);
            chartData.put("revenues", revenues);
            // Thêm orders data nếu cần (có thể tính từ revenues hoặc để trống)
            chartData.put("orders", revenues.stream().map(Double::intValue).collect(Collectors.toList()));

            log.info("Sales chart data loaded for {} days", days);
            return ApiResponse.<Map<String, Object>>builder()
                    .status(200)
                    .message("Sales chart data retrieved successfully")
                    .data(chartData)
                    .build();
        } catch (Exception e) {
            log.error("Error loading sales chart data: {}", e.getMessage());
            // Fallback data
            Map<String, Object> fallbackData = new HashMap<>();
            List<String> fallbackLabels = new ArrayList<>();
            List<Double> fallbackRevenues = new ArrayList<>();

            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                fallbackLabels.add(date.format(DateTimeFormatter.ofPattern("dd/MM")));
                fallbackRevenues.add(0.0);
            }

            fallbackData.put("labels", fallbackLabels);
            fallbackData.put("revenues", fallbackRevenues);
            fallbackData.put("orders", new ArrayList<>());

            return ApiResponse.<Map<String, Object>>builder()
                    .status(500)
                    .message("Error loading sales chart data: " + e.getMessage())
                    .data(fallbackData)
                    .build();
        }
    }

    @Override
    public ApiResponse<Void> deleteCustomer(Long id) {
        try {
            customerRepository.deleteById(id);
            log.info("Customer with id {} deleted successfully", id);
            return ApiResponse.<Void>builder()
                    .status(200)
                    .message("Customer deleted successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting customer with id {}: {}", id, e.getMessage());
            return ApiResponse.<Void>builder()
                    .status(500)
                    .message("Error deleting customer: " + e.getMessage())
                    .build();
        }
    }

    // ========== MAPPING METHODS ==========

    private CustomerResponseDTO mapToCustomerDTO(com.project.management_system.model.Customer customer) {
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

    private OrderResponseDTO mapToOrderDTO(com.project.management_system.model.Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        // Sửa: Sử dụng setter phù hợp với OrderResponseDTO
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

    private ProductResponseDTO mapToProductDTO(com.project.management_system.model.Product product) {
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