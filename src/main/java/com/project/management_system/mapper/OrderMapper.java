package com.project.management_system.mapper;

import com.project.management_system.dto.response.OrderItemResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.model.Order;
import com.project.management_system.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class OrderMapper {

    public OrderResponseDTO toDTO(Order order) {
        if (order == null) return null;

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderCode(order.getOrderCode());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());

        if (order.getCustomer() != null) {
            dto.setCustomerName(order.getCustomer().getName());
        }

        if (order.getCreatedBy() != null) {
            dto.setCreatedBy(order.getCreatedBy().getUsername());
        }

        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethod(order.getPaymentMethod().getName());
        }

        if (order.getItems() != null) {
            List<OrderItemResponseDTO> items = order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        return dto;
    }

    private OrderItemResponseDTO toItemDTO(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductName(item.getProduct() != null ? item.getProduct().getName() : "");
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
}
