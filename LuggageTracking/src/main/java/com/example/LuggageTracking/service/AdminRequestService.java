package com.example.LuggageTracking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LuggageTracking.dto.AdminRequestDTO;
import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.exception.ValidationException;
import com.example.LuggageTracking.model.AdminRequest;
import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.repository.AdminRequestRepository;
import com.example.LuggageTracking.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRequestService {
    
    @Autowired
    private AdminRequestRepository adminRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<AdminRequestDTO> getPendingRequests() {
        List<AdminRequest> requests = adminRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<AdminRequestDTO> getAllRequests() {
        List<AdminRequest> requests = adminRequestRepository.findAll();
        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public void approveRequest(Long requestId, String approvedBy) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin request not found"));
        
        if (!"PENDING".equals(request.getStatus())) {
            throw new ValidationException("Request already processed");
        }
        
        // Update request status
        request.setStatus("APPROVED");
        request.setApprovedAt(LocalDateTime.now());
        request.setApprovedBy(approvedBy);
        adminRequestRepository.save(request);
        
        // Update user to approved admin
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsApproved(true);
        userRepository.save(user);
    }
    
    @Transactional
    public void rejectRequest(Long requestId, String approvedBy) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin request not found"));
        
        if (!"PENDING".equals(request.getStatus())) {
            throw new ValidationException("Request already processed");
        }
        
        // Update request status
        request.setStatus("REJECTED");
        request.setApprovedAt(LocalDateTime.now());
        request.setApprovedBy(approvedBy);
        adminRequestRepository.save(request);
        
        // Optionally: Delete the user or keep them as rejected
        // For now, we'll just mark them as not approved
    }
    
    private AdminRequestDTO convertToDTO(AdminRequest request) {
        AdminRequestDTO dto = new AdminRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
        dto.setStatus(request.getStatus());
        dto.setRequestMessage(request.getRequestMessage());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
