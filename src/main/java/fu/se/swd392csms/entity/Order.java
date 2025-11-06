package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity
 * Represents customer orders in the coffee shop
 */
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee; // Employee who created the order
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    private String status; // Pending, Preparing, Completed, Cancelled
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}
