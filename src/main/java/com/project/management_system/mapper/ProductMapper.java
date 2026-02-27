package com.project.management_system.mapper;

import com.project.management_system.dto.request.ProductRequestDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity -> Response DTO
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "unit.name", target = "unitName")
    ProductResponseDTO toDTO(Product product);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Product toEntity(ProductRequestDTO dto);
}



