package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides CRUD operations and custom queries for users
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * @param username Username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Find user by username with roles eagerly loaded
     * @param username Username
     * @return Optional containing the user with roles
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(String username);
}
