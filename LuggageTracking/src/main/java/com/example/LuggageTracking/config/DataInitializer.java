package com.example.LuggageTracking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create super admin if not exists
        if (!userRepository.existsByUsername("Samarth")) {
            User superAdmin = new User();
            superAdmin.setUsername("Samarth");
            superAdmin.setEmail("samarth@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("deshmukh123"));
            superAdmin.setRole("ADMIN");
            superAdmin.setIsApproved(true); // Super admin is always approved
            userRepository.save(superAdmin);
            System.out.println("✅ Super Admin 'Samarth' created successfully!");
        }
    }
}
