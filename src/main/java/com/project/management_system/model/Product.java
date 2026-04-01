package com.project.management_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal importPrice;
    private BigDecimal salePrice;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    private LocalDateTime createdAt = LocalDateTime.now();
    // Thêm field mới
    @Column(nullable = false)
    private boolean deleted = false;

    // Thêm field để lưu thời gian xóa (tùy chọn)
    private LocalDateTime deletedAt;

    // Thêm field để lưu người xóa (tùy chọn)
    private String deletedBy;
    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        OUT_OF_STOCK,
        DELETED  // Thêm status mới
    }
}

