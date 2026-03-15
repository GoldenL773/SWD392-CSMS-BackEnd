package com.csms.auth.controller;

import com.csms.auth.dto.AuthRequest;
import com.csms.auth.dto.AuthResponse;
import com.csms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody com.csms.auth.dto.RegisterRequest registerRequest) {
        com.csms.auth.entity.User user = authService.registerUser(registerRequest);
        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "userId", user.getId(),
            "username", user.getUsername()
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}
