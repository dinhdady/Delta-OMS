package com.project.management_system.service.impl;

import com.project.management_system.dto.request.ProductRequestDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.exception.BadRequestException;
import com.project.management_system.exception.ResourceNotFoundException;
import com.project.management_system.mapper.ProductMapper;
import com.project.management_system.model.Product;
import com.project.management_system.payload.ApiResponse;
import com.project.management_system.repository.CategoryRepository;
import com.project.management_system.repository.ProductRepository;
import com.project.management_system.repository.UnitRepository;
import com.project.management_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;

    @Override
    public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO dto) {

        if (productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product SKU already exists");
        }

        Product product = productMapper.toEntity(dto);

        product.setCategory(
                categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"))
        );

        product.setUnit(
                unitRepository.findById(dto.getUnitId())
                        .orElseThrow(() -> new ResourceNotFoundException("Unit not found"))
        );

        Product savedProduct = productRepository.save(product);

        return ApiResponse.<ProductResponseDTO>builder()
                .status(201)
                .message("Product created successfully")
                .data(productMapper.toDTO(savedProduct))
                .build();
    }

    @Override
    public ApiResponse<ProductResponseDTO> updateProduct(Long id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check SKU nếu thay đổi
        if (!product.getSku().equals(dto.getSku())
                && productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product SKU already exists");
        }

        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setSalePrice(dto.getSalePrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus());

        Product updated = productRepository.save(product);

        return ApiResponse.<ProductResponseDTO>builder()
                .status(200)
                .message("Product updated successfully")
                .data(productMapper.toDTO(updated))
                .build();
    }

    @Override
    public ApiResponse<ProductResponseDTO> getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ApiResponse.<ProductResponseDTO>builder()
                .status(200)
                .message("Product retrieved successfully")
                .data(productMapper.toDTO(product))
                .build();
    }

    @Override
    public ApiResponse<List<ProductResponseDTO>> getAllProducts() {

        List<ProductResponseDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();

        return ApiResponse.<List<ProductResponseDTO>>builder()
                .status(200)
                .message("Product list retrieved successfully")
                .data(products)
                .build();
    }

    @Override
    public ApiResponse<Void> deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productRepository.delete(product);

        return ApiResponse.<Void>builder()
                .status(204)
                .message("Product deleted successfully")
                .data(null)
                .build();
    }
}