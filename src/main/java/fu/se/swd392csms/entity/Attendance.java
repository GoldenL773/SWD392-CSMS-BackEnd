package fu.se.swd392csms.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
 * Attendance Entity
 * Tracks employee check-in and check-out times
 */
@Entity
@Table(name = "attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private LocalDate date;
    
    private LocalTime checkInTime;
    
    private LocalTime checkOutTime;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal workingHours; // Calculated total working hours

    @Column(precision = 5, scale = 2)
    private BigDecimal totalHours; // Total hours worked (alias for workingHours)

    @Column(precision = 5, scale = 2)
    private BigDecimal overtimeHours; // Calculated overtime hours

    @Column(nullable = false)
    private String status; // Present, Absent, Late

    private String notes; // Additional notes about attendance
}
