package fu.se.swd392csms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Salary Entity
 * Represents employee monthly salary records
 */
@Entity
@Table(name = "salaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private Integer month; // 1-12
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal baseSalary;
    
    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;
    
    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal deduction = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal deductions = BigDecimal.ZERO; // Alias for deduction (plural form)

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalSalary; // baseSalary + bonus - deduction

    private LocalDateTime paymentDate; // Date when salary was paid

    @Column(nullable = false)
    private String status; // Pending, Paid

    private String notes; // Additional notes about salary payment
}
