//package com.example.LuggageTracking.controller;
//
//
//import com.example.LuggageTracking.dto.LoginRequest;
//import com.example.LuggageTracking.dto.RegisterRequest;
//import com.example.LuggageTracking.dto.AuthResponse;
//import com.example.LuggageTracking.exception.UnauthorizedException;
//import com.example.LuggageTracking.exception.ValidationException;
//import com.example.LuggageTracking.model.User;
//import com.example.LuggageTracking.security.JwtUtil;
//import com.example.LuggageTracking.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
//public class AuthController {
//    
//    @Autowired
//    private UserService userService;
//    
//    @Autowired
//    private JwtUtil jwtUtil;
//    
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
//        try {
//            User user = userService.registerUser(request);
//            
//            AuthResponse response = new AuthResponse();
//            response.setUsername(user.getUsername());
//            response.setRole(user.getRole());
//            
//            // If admin and not approved, inform user
//            if ("ADMIN".equalsIgnoreCase(user.getRole()) && !user.getIsApproved()) {
//                response.setMessage("Your admin request has been submitted. Please wait for approval from the super admin.");
//                response.setRequiresApproval(true);
//                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
//            }
//            
//            // Generate token for approved users
//            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
//            response.setToken(token);
//            response.setMessage("Registration successful!");
//            
//            return ResponseEntity.ok(response);
//        } catch (ValidationException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//    
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            // Authenticate user
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//            
//            // Get user details
//            User user = userService.findByUsername(request.getEmail())
//                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
//            
//            // Check if admin is approved
//            if ("ADMIN".equalsIgnoreCase(user.getRole()) && !user.getIsApproved()) {
//                AuthResponse response = new AuthResponse();
//                response.setMessage("Your admin request is pending approval. Please wait for the super admin to approve your request.");
//                response.setRequiresApproval(true);
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//            }
//            
//            // Generate token
//            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
//            
//            AuthResponse response = new AuthResponse(token, user.getRole(), user.getUsername());
//            response.setMessage("Login successful!");
//            
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Invalid username or password");
//        }
//    }
//    
//    @GetMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
//        try {
//            if (token != null && token.startsWith("Bearer ")) {
//                String jwt = token.substring(7);
//                String username = jwtUtil.extractUsername(jwt);
//                String role = jwtUtil.extractRole(jwt);
//                
//                if (jwtUtil.validateToken(jwt, username)) {
//                    AuthResponse response = new AuthResponse();
//                    response.setUsername(username);
//                    response.setRole(role);
//                    response.setMessage("Token is valid");
//                    return ResponseEntity.ok(response);
//                }
//            }
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//        }
//    }
//}

package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.dto.LoginRequest;
import com.example.LuggageTracking.dto.RegisterRequest;
import com.example.LuggageTracking.dto.AuthResponse;
import com.example.LuggageTracking.exception.UnauthorizedException;
import com.example.LuggageTracking.exception.ValidationException;
import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.security.JwtUtil;
import com.example.LuggageTracking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            
            AuthResponse response = new AuthResponse();
            response.setUsername(user.getUsername() != null ? user.getUsername() : user.getEmail());
            response.setRole(user.getRole());
            
            // If admin and not approved, inform user
            if ("ADMIN".equalsIgnoreCase(user.getRole()) && !user.getIsApproved()) {
                response.setMessage("Your admin request has been submitted. Please wait for approval from the super admin.");
                response.setRequiresApproval(true);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            }
            
            // Generate token for approved users (token subject = email)
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            response.setToken(token);
            response.setMessage("Registration successful!");
            
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user using email as principal
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get user details by email
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
            
            // Check if admin is approved
            if ("ADMIN".equalsIgnoreCase(user.getRole()) && !user.getIsApproved()) {
                AuthResponse response = new AuthResponse();
                response.setMessage("Your admin request is pending approval. Please wait for the super admin to approve your request.");
                response.setRequiresApproval(true);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Generate token with email as subject
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            
            AuthResponse response = new AuthResponse(token, user.getRole(), user.getEmail());
            response.setMessage("Login successful!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String email = jwtUtil.extractUsername(jwt); // username here = email
                String role = jwtUtil.extractRole(jwt);
                
                if (jwtUtil.validateToken(jwt, email)) {
                    AuthResponse response = new AuthResponse();
                    response.setUsername(email);
                    response.setRole(role);
                    response.setMessage("Token is valid");
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}

