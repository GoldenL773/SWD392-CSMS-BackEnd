package com.csms.order.repository;

import com.csms.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate)")
    Page<Order> findByFilters(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("startDate") LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStatus(String status, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
}
