package com.wallet.biochain.controllers;

import com.wallet.biochain.dto.UserDTO;
import com.wallet.biochain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user in the system")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        log.info("REST request to register new user: {}", userDTO.getUsername());

        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            log.error("Failed to register user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns user details")
    public ResponseEntity<UserDTO> login(
            @RequestParam String username,
            @RequestParam String password) {
        log.info("REST request to login user: {}", username);

        try {
            UserDTO user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            
            // Note: In production, implement proper password verification and JWT token generation
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
