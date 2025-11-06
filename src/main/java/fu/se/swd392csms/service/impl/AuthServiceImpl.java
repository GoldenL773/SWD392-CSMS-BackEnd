package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.LoginRequest;
import fu.se.swd392csms.dto.request.RegisterRequest;
import fu.se.swd392csms.dto.response.LoginResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.entity.Role;
import fu.se.swd392csms.entity.User;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.repository.RoleRepository;
import fu.se.swd392csms.repository.UserRepository;
import fu.se.swd392csms.security.JwtTokenProvider;
import fu.se.swd392csms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication Service Implementation
 * Implements user authentication and registration logic
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);
        
        // Get user details
        User user = userRepository.findByUsernameWithRoles(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.getUsername()));
        
        // Get employee details if exists
        Employee employee = employeeRepository.findByUserId(user.getId()).orElse(null);
        
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        
        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .fullName(employee != null ? employee.getFullName() : user.getUsername())
                .roles(roles)
                .build();
    }
    
    @Override
    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        
        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(new HashSet<>())
                .build();
        
        // Assign roles
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            // Default role: STAFF
            Role staffRole = roleRepository.findByName("ROLE_STAFF")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_STAFF"));
            roles.add(staffRole);
        } else {
            strRoles.forEach(roleName -> {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                roles.add(role);
            });
        }
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        
        // Create employee record
        Employee employee = Employee.builder()
                .fullName(registerRequest.getFullName())
                .phone(registerRequest.getPhone())
                .position("Staff") // Default position
                .hireDate(LocalDate.now())
                .status("Active")
                .user(savedUser)
                .build();
        
        employeeRepository.save(employee);
        
        return new MessageResponse("User registered successfully");
    }
    
    @Override
    public LoginResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        User user = userRepository.findByUsernameWithRoles(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));
        
        Employee employee = employeeRepository.findByUserId(user.getId()).orElse(null);
        
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        
        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(employee != null ? employee.getFullName() : user.getUsername())
                .roles(roles)
                .build();
    }
}
