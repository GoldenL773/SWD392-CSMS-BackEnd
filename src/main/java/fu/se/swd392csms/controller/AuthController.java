package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.LoginRequest;
import fu.se.swd392csms.dto.request.RegisterRequest;
import fu.se.swd392csms.dto.response.LoginResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user authentication and registration endpoints
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * User login endpoint
     * @param loginRequest Login credentials
     * @return JWT token and user information
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * User registration endpoint
     * @param registerRequest Registration details
     * @return Success message
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user account")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        MessageResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current authenticated user
     * @return Current user information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<LoginResponse> getCurrentUser() {
        LoginResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}
