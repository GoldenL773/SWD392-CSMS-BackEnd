package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Ingredient Entity
 * Represents raw materials used in products
 */
@Entity
@Table(name = "ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String unit; // kg, grams, liters, etc.
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity; // Current stock quantity

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal minimumStock = BigDecimal.ZERO; // Minimum stock threshold for alerts

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal pricePerUnit; // Cost per unit

    private String supplier;
}
