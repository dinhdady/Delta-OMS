package com.project.management_system.service;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    CategoryResponseDTO create(CategoryRequestDTO dto);

    CategoryResponseDTO update(Long id, CategoryRequestDTO dto);

    CategoryResponseDTO getById(Long id);

    List<CategoryResponseDTO> getAll();

    void delete(Long id);
}
