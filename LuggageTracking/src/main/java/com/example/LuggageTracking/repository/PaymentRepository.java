package com.example.LuggageTracking.repository;

import com.example.LuggageTracking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payment by tracking ID
    Optional<Payment> findByTrackingId(String trackingId);
    
    // Find all payments for a user
    List<Payment> findByUserId(Long userId);
    
    // Find payments by status
    List<Payment> findByPaymentStatus(String paymentStatus);
    
    // Check if payment exists for tracking ID
    Boolean existsByTrackingId(String trackingId);
    
    // Find all completed payments
    List<Payment> findByPaymentStatusOrderByCompletedAtDesc(String paymentStatus);
}