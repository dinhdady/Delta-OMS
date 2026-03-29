package com.project.management_system.repository;

import com.project.management_system.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByCustomerId(Long customerId);

    // Hoặc dùng derived query
    List<Order> findTop10ByOrderByOrderDateDesc();

    // Tính tổng doanh thu (dùng finalAmount)
    @Query("SELECT SUM(o.finalAmount) FROM Order o")
    Double getTotalRevenue();

    // Lấy doanh thu theo tháng
    @Query("SELECT FUNCTION('MONTH', o.orderDate) as month, SUM(o.finalAmount) as revenue " +
            "FROM Order o " +
            "WHERE o.orderDate IS NOT NULL " +
            "AND o.orderStatus != 'CANCELLED' " +
            "GROUP BY FUNCTION('MONTH', o.orderDate) " +
            "ORDER BY month")
    List<Object[]> getMonthlySales();

    // Thống kê theo năm
    @Query("SELECT FUNCTION('YEAR', o.orderDate) as year, " +
            "FUNCTION('MONTH', o.orderDate) as month, " +
            "SUM(o.finalAmount) as revenue " +
            "FROM Order o " +
            "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getSalesByMonth();
        // Tìm orders theo customer id và sắp xếp theo ngày tạo giảm dần
        List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

        // Lấy tất cả orders sắp xếp theo ngày tạo giảm dần
        List<Order> findAllByOrderByOrderDateDesc();


        // Lấy orders theo trạng thái
        List<Order> findByOrderStatus(String orderStatus);

        // Lấy orders theo khoảng thời gian
        List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Thống kê số lượng orders theo trạng thái
        @Query("SELECT o.orderStatus, COUNT(o) FROM Order o GROUP BY o.orderStatus")
        List<Object[]> countOrdersByStatus();


        // Tính tổng doanh thu theo tháng
        @Query("SELECT FUNCTION('MONTH', o.orderDate) as month, SUM(o.finalAmount) as revenue " +
                "FROM Order o " +
                "WHERE o.orderStatus = 'COMPLETED' " +
                "AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', o.orderDate) " +
                "ORDER BY month")
        List<Object[]> getMonthlyRevenue();

        // Tìm orders theo customer (JPQL join fetch để lấy items)
        @Query("SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product p " +
                "WHERE o.customer.id = :customerId " +
                "ORDER BY o.orderDate DESC")
        List<Order> findOrdersWithItemsByCustomerId(@Param("customerId") Long customerId);

        // Tìm order theo code (join fetch items)
        @Query("SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product p " +
                "WHERE o.orderCode = :orderCode")
        Optional<Order> findOrderWithItemsByCode(@Param("orderCode") String orderCode);

        // Lấy tất cả orders với items (phân trang)
        @Query("SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product p " +
                "ORDER BY o.orderDate DESC")
        List<Order> findAllOrdersWithItems(Pageable pageable);
    }

