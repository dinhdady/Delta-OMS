package com.project.management_system.service;

import com.project.management_system.dto.request.OrderItemRequestDTO;
import com.project.management_system.dto.request.OrderRequestDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.mapper.OrderMapper;
import com.project.management_system.model.*;
import com.project.management_system.repository.*;
import com.project.management_system.service.interfaceService.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        order.setOrderCode("ORD-" + System.currentTimeMillis());
        order.setCustomer(customer);
        order.setCreatedBy(user);
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(dto.getShippingAddress());
        order.setOrderStatus("NEW");
        order.setPaymentStatus("UNPAID");

        BigDecimal total = BigDecimal.ZERO;

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequestDTO itemDTO : dto.getItems()) {

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotalPrice(
                    itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
            );

            total = total.add(item.getTotalPrice());
            items.add(item);

            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
        }

        order.setTotalAmount(total);
        order.setFinalAmount(total);
        order.setItems(items);

        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public OrderResponseDTO getOrderByCode(String orderCode) {
        return orderMapper.toDTO(
                orderRepository.findByOrderCode(orderCode)
                        .orElseThrow(() -> new RuntimeException("Order not found"))
        );
    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toDTO)
                .toList();
    }
}

