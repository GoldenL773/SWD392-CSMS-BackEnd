package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * ProductIngredient Entity
 * Join table defining the quantity of ingredients required for each product
 */
@Entity
@Table(name = "product_ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantityRequired; // Amount of ingredient needed for this product
}
