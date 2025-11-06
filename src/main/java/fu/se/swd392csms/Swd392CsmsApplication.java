package fu.se.swd392csms;

import fu.se.swd392csms.entity.Role;
import fu.se.swd392csms.entity.User;
import fu.se.swd392csms.repository.RoleRepository;
import fu.se.swd392csms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Swd392CsmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Swd392CsmsApplication.class, args);
    }

    /**
     * Initialize database with default roles and admin user
     */
    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, 
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Create roles if they don't exist
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ADMIN");
                        return roleRepository.save(role);
                    });

            Role managerRole = roleRepository.findByName("MANAGER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("MANAGER");
                        return roleRepository.save(role);
                    });

            Role staffRole = roleRepository.findByName("STAFF")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("STAFF");
                        return roleRepository.save(role);
                    });

            Role financeRole = roleRepository.findByName("FINANCE")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("FINANCE");
                        return roleRepository.save(role);
                    });

            // Create admin user if doesn't exist
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                userRepository.save(admin);
                System.out.println("✅ Admin user created - Username: admin, Password: admin123");
            }

            // Create manager user if doesn't exist
            if (!userRepository.existsByUsername("manager")) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setPassword(passwordEncoder.encode("manager123"));
                Set<Role> managerRoles = new HashSet<>();
                managerRoles.add(managerRole);
                manager.setRoles(managerRoles);
                userRepository.save(manager);
                System.out.println("✅ Manager user created - Username: manager, Password: manager123");
            }

            // Create staff user if doesn't exist
            if (!userRepository.existsByUsername("staff")) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                Set<Role> staffRoles = new HashSet<>();
                staffRoles.add(staffRole);
                staff.setRoles(staffRoles);
                userRepository.save(staff);
                System.out.println("✅ Staff user created - Username: staff, Password: staff123");
            }

            System.out.println("🎉 Database initialization complete!");
            System.out.println("📝 Default credentials:");
            System.out.println("   Admin    - Username: admin    | Password: admin123");
            System.out.println("   Manager  - Username: manager  | Password: manager123");
            System.out.println("   Staff    - Username: staff    | Password: staff123");
        };
    }
}
