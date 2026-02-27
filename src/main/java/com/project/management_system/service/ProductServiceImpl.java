package com.project.management_system.service;

import com.project.management_system.dto.request.ProductRequestDTO;
import com.project.management_system.dto.response.ProductResponseDTO;
import com.project.management_system.mapper.ProductMapper;
import com.project.management_system.model.Product;
import com.project.management_system.repository.CategoryRepository;
import com.project.management_system.repository.ProductRepository;
import com.project.management_system.repository.UnitRepository;
import com.project.management_system.service.interfaceService.ProductService;
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
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = productMapper.toEntity(dto);

        product.setCategory(
                categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"))
        );

        product.setUnit(
                unitRepository.findById(dto.getUnitId())
                        .orElseThrow(() -> new RuntimeException("Unit not found"))
        );

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setSalePrice(dto.getSalePrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus());

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return productMapper.toDTO(
                productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found"))
        );
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

