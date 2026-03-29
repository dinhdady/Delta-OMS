package com.project.management_system.controller;

import com.project.management_system.model.Category;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ApiResponse<List<Category>> getAll() {
        return ApiResponse.<List<Category>>builder()
                .status(200)
                .message("Categories retrieved")
                .data(categoryRepository.findAll())
                .build();
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        return ApiResponse.<Category>builder()
                .status(201)
                .message("Category created")
                .data(categoryRepository.save(category))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(
            @PathVariable Long id,
            @RequestBody Category category) {

        Category existing = categoryRepository.findById(id).orElseThrow();

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());

        return ApiResponse.<Category>builder()
                .status(200)
                .message("Category updated")
                .data(categoryRepository.save(existing))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {

        categoryRepository.deleteById(id);

        return ApiResponse.<String>builder()
                .status(200)
                .message("Category deleted")
                .data("Deleted successfully")
                .build();
    }
}