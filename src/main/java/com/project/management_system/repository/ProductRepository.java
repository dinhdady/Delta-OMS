package com.project.management_system.repository;

import com.project.management_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsBySku(String sku);
    List<Product> findTop5ByOrderByQuantityDesc();

    // Cách 2: Dùng @Query với Pageable
    @Query("SELECT p FROM Product p ORDER BY p.quantity DESC")
    List<Product> findTopProducts(Pageable pageable);

    // Cách 3: Nếu muốn lấy sản phẩm bán chạy dựa trên số lượng đã bán (cần có order_items)
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN OrderItem oi ON oi.product.id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC")
    List<Product> findBestSellingProducts(Pageable pageable);
}
