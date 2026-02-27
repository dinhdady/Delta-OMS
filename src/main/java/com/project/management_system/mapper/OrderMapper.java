package com.project.management_system.mapper;

import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class}
)
public interface OrderMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "createdBy.fullName", target = "createdBy")
    @Mapping(source = "paymentMethod.name", target = "paymentMethod")
    OrderResponseDTO toDTO(Order order);
}

