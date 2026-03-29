package com.project.management_system.service.impl;

import com.project.management_system.dto.request.OrderItemRequestDTO;
import com.project.management_system.dto.request.OrderRequestDTO;
import com.project.management_system.dto.response.OrderItemResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.model.*;
import com.project.management_system.repository.*;
import com.project.management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.project.management_system.mapper.OrderMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCustomer(customer);
        order.setCreatedBy(user);
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(dto.getShippingAddress());
        order.setNote(dto.getNote());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            // Tính giá nếu không có unitPrice từ request
            BigDecimal unitPrice = itemDTO.getUnitPrice();
            if (unitPrice == null) {
                unitPrice = product.getSalePrice();
            }

            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(totalPrice);

            total = total.add(totalPrice);
            items.add(item);

            // Cập nhật số lượng tồn kho
            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setFinalAmount(total);
        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        // Lưu items
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
        }

        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderCode));
        return orderMapper.toDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    private String generateOrderCode() {
        return "ORD" + System.currentTimeMillis();
    }
}