package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.LoginRequest;
import fu.se.swd392csms.dto.request.RegisterRequest;
import fu.se.swd392csms.dto.response.LoginResponse;
import fu.se.swd392csms.dto.response.MessageResponse;

/**
 * Authentication Service Interface
 * Handles user authentication and registration
 */
public interface AuthService {
    
    /**
     * Authenticate user and generate JWT token
     * @param loginRequest Login credentials
     * @return Login response with JWT token
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Register new user account
     * @param registerRequest Registration details
     * @return Success message
     */
    MessageResponse register(RegisterRequest registerRequest);
    
    /**
     * Get current authenticated user information
     * @return Login response with user details
     */
    LoginResponse getCurrentUser();
}
