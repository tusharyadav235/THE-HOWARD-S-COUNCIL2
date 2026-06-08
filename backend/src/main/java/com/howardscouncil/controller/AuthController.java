package com.howardscouncil.controller;

import com.howardscouncil.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * POST /api/auth/login
     * Body: { "username": "admin", "password": "Howard@Admin2024" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.get("username"),
                            request.get("password")
                    )
            );

            String token = jwtUtil.generateToken(auth.getName());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "username", auth.getName(),
                    "message", "Login successful"
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Invalid username or password"
            ));
        }
    }

    /**
     * GET /api/auth/verify — verify if token is still valid
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));
    }
}
