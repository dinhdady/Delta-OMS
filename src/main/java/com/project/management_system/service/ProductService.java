package com.project.management_system.service;

import com.project.management_system.dto.request.ProductRequestDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.model.Product;
import com.project.management_system.payload.ApiResponse;

import java.util.List;

public interface ProductService {

    ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO dto);

    ApiResponse<ProductResponseDTO> updateProduct(Long id, ProductRequestDTO dto);

    ApiResponse<ProductResponseDTO> getProductById(Long id);

    ApiResponse<List<ProductResponseDTO>> getAllProducts();

    ApiResponse<Void> deleteProduct(Long id);
    public List<Product> getAllActiveProducts();
    public Product getActiveProductById(Long id);
    ApiResponse<List<ProductResponseDTO>> getAllProductsIncludingDeleted();
    ApiResponse<Void> restoreProduct(Long id);
    ApiResponse<Void> permanentlyDeleteProduct(Long id);
}

