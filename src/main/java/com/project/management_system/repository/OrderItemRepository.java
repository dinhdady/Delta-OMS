package com.project.management_system.repository;


import com.project.management_system.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Tìm items theo order id
    List<OrderItem> findByOrderId(Long orderId);

    // Tìm items theo order code
    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.orderCode = :orderCode")
    List<OrderItem> findByOrderCode(@Param("orderCode") String orderCode);

    // Thống kê sản phẩm bán chạy
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> getBestSellingProducts(Pageable pageable);

    // Tính doanh thu theo sản phẩm
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.totalPrice) as totalRevenue " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getProductRevenue(Pageable pageable);
}