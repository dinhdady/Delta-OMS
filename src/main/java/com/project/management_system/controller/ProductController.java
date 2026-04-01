package com.project.management_system.controller;

import com.project.management_system.dto.request.ProductRequestDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.ok(productService.createProduct(dto));
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProductsIncludingDeleted() {
        return ResponseEntity.ok(productService.getAllProductsIncludingDeleted());
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> restoreProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.restoreProduct(id));
    }
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.permanentlyDeleteProduct(id));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}