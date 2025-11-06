package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product Entity
 * Represents menu items that can be sold in the coffee shop
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String category; // Coffee, Tea, Cake, Pastry, etc.
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String status; // Available, Unavailable
    
    private String description;
}
