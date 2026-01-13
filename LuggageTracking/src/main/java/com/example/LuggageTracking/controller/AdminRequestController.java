package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.dto.AdminRequestDTO;
import com.example.LuggageTracking.security.JwtUtil;
import com.example.LuggageTracking.service.AdminRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/requests")
@CrossOrigin(origins = "*")
public class AdminRequestController {
    
    @Autowired
    private AdminRequestService adminRequestService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AdminRequestDTO>> getPendingRequests() {
        List<AdminRequestDTO> requests = adminRequestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AdminRequestDTO>> getAllRequests() {
        List<AdminRequestDTO> requests = adminRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }
    
    @PostMapping("/{requestId}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String approvedBy = jwtUtil.extractUsername(jwt);
            
            adminRequestService.approveRequest(requestId, approvedBy);
            return ResponseEntity.ok(Map.of("message", "Admin request approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String approvedBy = jwtUtil.extractUsername(jwt);
            
            adminRequestService.rejectRequest(requestId, approvedBy);
            return ResponseEntity.ok(Map.of("message", "Admin request rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}