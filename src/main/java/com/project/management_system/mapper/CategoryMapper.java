package com.project.management_system.mapper;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;
import com.project.management_system.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toDTO(Category category);

    Category toEntity(CategoryRequestDTO dto);
}