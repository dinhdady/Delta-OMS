package com.project.management_system.repository;

import com.project.management_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // Lấy top sản phẩm bán chạy - sử dụng JOIN với OrderItem
    @Query("SELECT p, SUM(oi.quantity) as totalSold " +
            "FROM Product p " +
            "LEFT JOIN OrderItem oi ON oi.product.id = p.id " +
            "LEFT JOIN Order o ON o.id = oi.order.id " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "GROUP BY p.id " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();
    // Cách 3: Nếu muốn lấy sản phẩm bán chạy dựa trên số lượng đã bán (cần có order_items)
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN OrderItem oi ON oi.product.id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC")
    List<Product> findBestSellingProducts(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActive();
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.id = :id")
    Optional<Product> findActiveById(@Param("id") Long id);
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.category.id = :categoryId")
    List<Product> findActiveByCategoryId(@Param("categoryId") Long categoryId);
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchActiveProducts(@Param("keyword") String keyword);

    // Đếm số lượng sản phẩm active
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deleted = false")
    long countActive();
    // Lấy top 10 sản phẩm bán chạy
    default List<Product> findTop10SellingProducts() {
        List<Object[]> results = findTopSellingProducts();
        List<Product> products = new java.util.ArrayList<>();
        for (int i = 0; i < Math.min(10, results.size()); i++) {
            products.add((Product) results.get(i)[0]);
        }
        return products;
    }
    // Lấy tất cả sản phẩm kể cả đã xóa (cho admin)
    @Query("SELECT p FROM Product p")
    List<Product> findAllIncludingDeleted();
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi WHERE oi.product.id = :productId")
    boolean hasOrderItems(@Param("productId") Long productId);
    // Lấy top 10 sản phẩm bán chạy nhất (dựa trên số lượng đã bán)

    // Lấy sản phẩm theo category
    List<Product> findByCategoryId(Long categoryId);

    // Đếm số lượng sản phẩm còn hàng
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0")
    long countAvailableProducts();
    // Lấy top 10 sản phẩm theo số lượng tồn kho (nếu chưa có soldQuantity)
    List<Product> findTop10ByOrderByQuantityDesc();
    // Lấy sản phẩm sắp hết hàng (quantity < threshold)
    List<Product> findByQuantityLessThan(int threshold);
}
