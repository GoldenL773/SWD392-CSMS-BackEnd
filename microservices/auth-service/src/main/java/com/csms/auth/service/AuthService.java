package com.csms.auth.service;

import com.csms.auth.dto.AuthRequest;
import com.csms.auth.dto.AuthResponse;
import com.csms.auth.dto.RegisterRequest;
import com.csms.auth.entity.Role;
import com.csms.auth.entity.User;
import com.csms.auth.repository.RoleRepository;
import com.csms.auth.repository.UserRepository;
import com.csms.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.csms.auth.client.EmployeeClient;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeClient employeeClient;

    public AuthResponse authenticateUser(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginRequest.getUsername()));

        String jwt = tokenProvider.generateToken(authentication, user.getId());
        
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }
    
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_STAFF")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_STAFF");
                        newRole.setDescription("Default Staff Role");
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
        } else {
            registerRequest.getRoles().forEach(role -> {
                String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
                Role r = roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            newRole.setDescription("Auto-created Role");
                            return roleRepository.save(newRole);
                        });
                roles.add(r);
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Task 9: Create default employee record
        try {
            EmployeeClient.EmployeeRegistrationRequest employeeRequest = EmployeeClient.EmployeeRegistrationRequest.builder()
                    .userId(savedUser.getId())
                    .firstName("") 
                    .lastName("")  
                    .position("STAFF") 
                    .hireDate(java.time.LocalDate.now())
                    .build();
            employeeClient.createEmployee(employeeRequest);
            log.info("Automatically created employee record for user: {}", savedUser.getUsername());
        } catch (Exception e) {
            log.error("Failed to create default employee record for user: {}. Error: {}", savedUser.getUsername(), e.getMessage());
        }

        return savedUser;
    }
}
