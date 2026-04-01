package com.project.management_system.controller;

import com.project.management_system.dto.request.CategoryRequestDTO;
import com.project.management_system.dto.response.CategoryResponseDTO;
import com.project.management_system.model.Category;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CategoryRepository;
import com.project.management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> create(@RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO saved = categoryService.create(dto);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponseDTO>builder()
                        .status(201)
                        .message("Category created successfully")
                        .data(saved)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO updated = categoryService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponseDTO>builder()
                        .status(200)
                        .message("Category updated")
                        .data(updated)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Category deleted")
                        .build()
        );
    }
}