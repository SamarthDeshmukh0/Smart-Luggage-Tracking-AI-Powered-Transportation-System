//package com.example.LuggageTracking.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "users")
//public class User {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(nullable = false, unique = true)
//    private String username;
//    
//    @Column(nullable = false)
//    private String password;
//    
//    @Column(nullable = false, unique = true)
//    private String email;
//    
//    @Column(nullable = false)
//    private String role; // ADMIN, CUSTOMER
//    
//    @Column(name = "is_approved")
//    private Boolean isApproved = false; // For admin approval
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//    
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//    
//    // Constructors
//    public User() {}
//    
//    public User(String username, String password, String email, String role) {
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.role = role;
//    }
//    
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//    
//    public void setId(Long id) {
//        this.id = id;
//    }
//    
//    public String getUsername() {
//        return username;
//    }
//    
//    public void setUsername(String username) {
//        this.username = username;
//    }
//    
//    public String getPassword() {
//        return password;
//    }
//    
//    public void setPassword(String password) {
//        this.password = password;
//    }
//    
//    public String getEmail() {
//        return email;
//    }
//    
//    public void setEmail(String email) {
//        this.email = email;
//    }
//    
//    public String getRole() {
//        return role;
//    }
//    
//    public void setRole(String role) {
//        this.role = role;
//    }
//    
//    public Boolean getIsApproved() {
//        return isApproved;
//    }
//    
//    public void setIsApproved(Boolean isApproved) {
//        this.isApproved = isApproved;
//    }
//    
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//    
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//    
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//    
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//}

//package com.example.LuggageTracking.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "users")
//public class User {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    // keep username if you want an optional display name; make it nullable
//    @Column(nullable = true)
//    private String username;
//    
//    @Column(nullable = false)
//    private String password;
//    
//    @Column(nullable = false, unique = true)
//    private String email;
//    
//    @Column(nullable = false)
//    private String role; // ADMIN, CUSTOMER
//    
//    @Column(name = "is_approved")
//    private Boolean isApproved = false; // For admin approval
//    
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//    
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//    
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//    
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//    
//    public User() {}
//    
//    // constructor for creating new users (no username required)
//    public User(String email, String password, String role) {
//        this.email = email;
//        this.password = password;
//        this.role = role;
//    }
//    
//    // Getters and Setters...
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public String getPassword() { return password; }
//    public void setPassword(String password) { this.password = password; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public String getRole() { return role; }
//    public void setRole(String role) { this.role = role; }
//
//    public Boolean getIsApproved() { return isApproved; }
//    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
//
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//
//    public LocalDateTime getUpdatedAt() { return updatedAt; }
//    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
//}



// REPLACE your User.java with this
// Location: src/main/java/com/example/LuggageTracking/model/User.java

package com.example.LuggageTracking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // FIX: Make username nullable and auto-generate from email
    @Column(nullable = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String role; // ADMIN, CUSTOMER
    
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // FIX: Auto-generate username from email if not provided
        if (username == null || username.trim().isEmpty()) {
            username = email.split("@")[0];
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public User() {}
    
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = email.split("@")[0]; // Auto-generate
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
