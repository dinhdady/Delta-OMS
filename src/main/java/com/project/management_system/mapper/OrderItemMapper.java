package com.project.management_system.mapper;

import com.project.management_system.dto.response.OrderItemResponseDTO;
import com.project.management_system.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toDTO(OrderItem orderItem);
}

