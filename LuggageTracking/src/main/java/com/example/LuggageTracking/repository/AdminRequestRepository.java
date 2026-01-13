package com.example.LuggageTracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LuggageTracking.model.AdminRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    List<AdminRequest> findByStatus(String status);
    List<AdminRequest> findByStatusOrderByCreatedAtDesc(String status);
    Optional<AdminRequest> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
    Boolean existsByUsername(String username);
}