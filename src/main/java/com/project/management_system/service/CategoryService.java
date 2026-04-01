package com.project.management_system.service;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;
import com.project.management_system.model.Category;
import com.project.management_system.payload.ApiResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    ApiResponse<List<CategoryResponseDTO>> getAll();

    Category save(Category category);

    Optional<Category> findById(Long id);

    void deleteById(Long id);

    // Nếu cần giữ lại các method cũ cho DTO
    CategoryResponseDTO create(CategoryRequestDTO dto);
    CategoryResponseDTO update(Long id, CategoryRequestDTO dto);
    CategoryResponseDTO getById(Long id);
    void delete(Long id);
}
