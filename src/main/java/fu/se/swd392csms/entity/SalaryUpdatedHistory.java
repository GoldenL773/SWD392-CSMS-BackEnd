package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * SalaryUpdatedHistory Entity
 * Audits all changes made to salary records
 */
@Entity
@Table(name = "salary_updated_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryUpdatedHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;
    
    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private Employee changedBy; // Employee who made the change
    
    @Column(nullable = false)
    private LocalDate changeDate;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal oldBaseSalary;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal newBaseSalary;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal oldBonus;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal newBonus;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal oldDeduction;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal newDeduction;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal oldTotalSalary;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal newTotalSalary;
    
    private String note; // Reason for the change
}
