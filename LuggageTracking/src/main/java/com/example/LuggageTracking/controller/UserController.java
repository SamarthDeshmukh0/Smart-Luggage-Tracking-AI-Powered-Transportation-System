package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.security.JwtUtil;
import com.example.LuggageTracking.service.UserService;
import com.example.LuggageTracking.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ShipmentService shipmentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get shipment count for this user
            int totalOrders = shipmentService.getShipmentsByUserId(user.getId()).size();
            
            // Build response
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("email", user.getEmail());
            profile.put("username", user.getUsername() != null ? user.getUsername() : user.getEmail());
            profile.put("role", user.getRole());
            profile.put("isApproved", user.getIsApproved());
            profile.put("createdAt", user.getCreatedAt());
            profile.put("totalOrders", totalOrders);
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> updates) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update allowed fields
            if (updates.containsKey("username")) {
                user.setUsername(updates.get("username"));
            }
            
            userService.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}