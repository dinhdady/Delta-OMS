package com.project.management_system.service.impl;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;
import com.project.management_system.model.Category;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CategoryRepository;
import com.project.management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public ApiResponse<List<CategoryResponseDTO>> getAll() {
        List<Category> allCategories = categoryRepository.findAll();

        // Lọc unique theo name, giữ lại bản ghi đầu tiên gặp
        Map<String, Category> uniqueMap = new LinkedHashMap<>();
        for (Category c : allCategories) {
            uniqueMap.putIfAbsent(c.getName(), c);
        }

        List<CategoryResponseDTO> categories = uniqueMap.values().stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getName()))
                .toList();

        return ApiResponse.<List<CategoryResponseDTO>>builder()
                .status(200)
                .message("Categories retrieved")
                .data(categories)
                .build();
    }


    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Giữ lại các method cũ
    @Override
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        return new CategoryResponseDTO(saved.getId(), saved.getName());
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getName());
        Category updated = categoryRepository.save(category);
        return new CategoryResponseDTO(updated.getId(), updated.getName());
    }

    @Override
    public CategoryResponseDTO getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}