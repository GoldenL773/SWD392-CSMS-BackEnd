package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee Entity
 * Represents staff members in the coffee shop
 * Linked to User entity for authentication
 */
@Entity
@Table(name = "employees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    private LocalDate dob; // Date of birth
    
    private String gender; // Male, Female, Other
    
    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String position; // Manager, Barista, Cashier, etc.

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(precision = 18, scale = 2)
    private BigDecimal salary; // Monthly salary

    @Column(nullable = false)
    private String status; // Active, Inactive

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
