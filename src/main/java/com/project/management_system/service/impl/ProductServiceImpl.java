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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        // Set default values for soft delete
        product.setDeleted(false);
        product.setDeletedAt(null);
        product.setDeletedBy(null);

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

        // Check if product is deleted
        if (product.isDeleted()) {
            throw new BadRequestException("Cannot update deleted product. Please restore it first.");
        }

        // Check SKU nếu thay đổi
        if (!product.getSku().equals(dto.getSku())
                && productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Product SKU already exists");
        }

        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImportPrice(dto.getImportPrice());
        product.setSalePrice(dto.getSalePrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus());

        // Update category if provided
        if (dto.getCategoryId() != null) {
            product.setCategory(
                    categoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found"))
            );
        }

        // Update unit if provided
        if (dto.getUnitId() != null) {
            product.setUnit(
                    unitRepository.findById(dto.getUnitId())
                            .orElseThrow(() -> new ResourceNotFoundException("Unit not found"))
            );
        }

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

        // Optional: Nếu muốn chỉ lấy sản phẩm chưa xóa
        // if (product.isDeleted()) {
        //     throw new ResourceNotFoundException("Product not found");
        // }

        return ApiResponse.<ProductResponseDTO>builder()
                .status(200)
                .message("Product retrieved successfully")
                .data(productMapper.toDTO(product))
                .build();
    }

    @Override
    public ApiResponse<List<ProductResponseDTO>> getAllProducts() {

        // Only get active products (not deleted)
        List<ProductResponseDTO> products = productRepository.findAllActive()
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

        // Check if already deleted
        if (product.isDeleted()) {
            throw new BadRequestException("Product is already deleted");
        }

        // Soft delete - chỉ đánh dấu là đã xóa, không xóa khỏi database
        product.setDeleted(true);
        product.setStatus(Product.ProductStatus.DELETED);
        product.setDeletedAt(LocalDateTime.now());

        // Get current username from security context (optional)
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            product.setDeletedBy(username);
        } catch (Exception e) {
            product.setDeletedBy("system");
        }

        productRepository.save(product);

        return ApiResponse.<Void>builder()
                .status(200)
                .message("Product deleted successfully")
                .data(null)
                .build();
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findAllActive();
    }

    @Override
    public Product getActiveProductById(Long id) {
        return productRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found"));
    }

    // Additional methods for soft delete management
    public ApiResponse<Void> restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.isDeleted()) {
            throw new BadRequestException("Product is not deleted");
        }

        product.setDeleted(false);
        product.setStatus(Product.ProductStatus.ACTIVE);
        product.setDeletedAt(null);
        product.setDeletedBy(null);

        productRepository.save(product);

        return ApiResponse.<Void>builder()
                .status(200)
                .message("Product restored successfully")
                .data(null)
                .build();
    }

    public ApiResponse<List<ProductResponseDTO>> getAllProductsIncludingDeleted() {
        List<ProductResponseDTO> products = productRepository.findAllIncludingDeleted()
                .stream()
                .map(productMapper::toDTO)
                .toList();

        return ApiResponse.<List<ProductResponseDTO>>builder()
                .status(200)
                .message("All products retrieved successfully")
                .data(products)
                .build();
    }

    public ApiResponse<Void> permanentlyDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if product has order items
        boolean hasOrderItems = productRepository.hasOrderItems(id);
        if (hasOrderItems) {
            throw new BadRequestException(
                    "Cannot permanently delete product because it has order history. " +
                            "Product is already soft deleted and can be restored if needed."
            );
        }

        productRepository.delete(product);

        return ApiResponse.<Void>builder()
                .status(200)
                .message("Product permanently deleted successfully")
                .data(null)
                .build();
    }
}