package com.project.management_system.service.impl;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.request.OrderItemRequestDTO;
import com.project.management_system.dto.request.OrderRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;
import com.project.management_system.dto.response.OrderResponseDTO;
import com.project.management_system.mapper.OrderMapper;
import com.project.management_system.model.*;
import com.project.management_system.repository.*;
import com.project.management_system.service.CategoryService;
import com.project.management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO dto) {

        Category category = new Category();
        category.setName(dto.getName());

        categoryRepository.save(category);

        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());

        categoryRepository.save(category);

        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    @Override
    public CategoryResponseDTO getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    @Override
    public List<CategoryResponseDTO> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getName()))
                .toList();
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}