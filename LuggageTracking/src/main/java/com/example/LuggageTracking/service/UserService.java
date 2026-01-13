//package com.example.LuggageTracking.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.LuggageTracking.dto.RegisterRequest;
//import com.example.LuggageTracking.exception.ValidationException;
//import com.example.LuggageTracking.model.AdminRequest;
//import com.example.LuggageTracking.model.User;
//import com.example.LuggageTracking.repository.AdminRequestRepository;
//import com.example.LuggageTracking.repository.UserRepository;
//
//import java.util.Optional;
//
//@Service
//public class UserService {
//    
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired
//    private AdminRequestRepository adminRequestRepository;
//    
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    
//    @Transactional
//    public User registerUser(RegisterRequest request) {
//        // Check if username already exists
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new ValidationException("Username already exists");
//        }
//        
//        // Check if email already exists
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new ValidationException("Email already exists");
//        }
//        
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(request.getRole());
//        
//        // If user requests ADMIN role, they need approval
//        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
//            user.setIsApproved(false); // Not approved by default
//            User savedUser = userRepository.save(user);
//            
//            // Create admin request
//            AdminRequest adminRequest = new AdminRequest(
//                savedUser.getId(),
//                request.getUsername(),
//                request.getEmail(),
//                request.getRequestMessage() != null ? request.getRequestMessage() : "Request to become admin"
//            );
//            adminRequestRepository.save(adminRequest);
//            
//            return savedUser;
//        } else {
//            // CUSTOMER role is auto-approved
//            user.setIsApproved(true);
//            return userRepository.save(user);
//        }
//    }
//    
//    public Optional<User> findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//    
//    public Optional<User> findById(Long id) {
//        return userRepository.findById(id);
//    }
//    
//    public User save(User user) {
//        return userRepository.save(user);
//    }
//    
//    public boolean authenticateUser(String username, String password) {
//        Optional<User> userOpt = userRepository.findByUsername(username);
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            return passwordEncoder.matches(password, user.getPassword());
//        }
//        return false;
//    }
//}

package com.example.LuggageTracking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LuggageTracking.dto.RegisterRequest;
import com.example.LuggageTracking.exception.ValidationException;
import com.example.LuggageTracking.model.AdminRequest;
import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.repository.AdminRequestRepository;
import com.example.LuggageTracking.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRequestRepository adminRequestRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = new User();
        // optional username can be set later or left null
        user.setUsername(null);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        // If user requests ADMIN role, they need approval
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            user.setIsApproved(false); // Not approved by default
            User savedUser = userRepository.save(user);
            
            // Create admin request record
            AdminRequest adminRequest = new AdminRequest(
                savedUser.getId(),
                savedUser.getUsername() != null ? savedUser.getUsername() : savedUser.getEmail(),
                savedUser.getEmail(),
                request.getRequestMessage() != null ? request.getRequestMessage() : "Request to become admin"
            );
            adminRequestRepository.save(adminRequest);
            
            return savedUser;
        } else {
            // CUSTOMER role is auto-approved
            user.setIsApproved(true);
            return userRepository.save(user);
        }
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public boolean authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
