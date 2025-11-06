package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DailyReport Entity
 * Stores aggregated data for daily business reports
 */
@Entity
@Table(name = "daily_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private LocalDate reportDate;
    
    private Integer totalOrders; // Total number of orders for the day
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalRevenue; // Total revenue for the day
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalIngredientCost; // Total cost of ingredients used
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalSalaryPaid; // Total salaries paid (if applicable)
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalWorkingHours; // Total hours worked by all employees
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy; // User who generated the report
    
    private LocalDateTime createdAt;
    
    private String notes;
}
