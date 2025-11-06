package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role Entity
 * Represents user roles in the system (ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF, ROLE_FINANCE)
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String name; // ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF, ROLE_FINANCE
}
