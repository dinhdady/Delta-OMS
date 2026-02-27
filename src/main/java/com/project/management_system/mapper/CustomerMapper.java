package com.project.management_system.mapper;

import com.project.management_system.dto.response.CustomerResponseDTO;
import com.project.management_system.dto.request.CustomerRequestDTO;
import com.project.management_system.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // Entity -> Response DTO
    @Mapping(source = "customerType.name", target = "customerTypeName")
    CustomerResponseDTO toDTO(Customer customer);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerType", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CustomerRequestDTO dto);
}

