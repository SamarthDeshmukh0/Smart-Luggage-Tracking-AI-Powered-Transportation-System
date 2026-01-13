package com.example.LuggageTracking.service;

import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.exception.ValidationException;
import com.example.LuggageTracking.model.Payment;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ShipmentService shipmentService;
    
    /**
     * Initiate a new payment for a shipment
     * @param trackingId - Shipment tracking ID
     * @param userId - User ID who is making payment
     * @param amount - Payment amount
     * @return Payment object with PENDING status
     */
    @Transactional
    public Payment initiatePayment(String trackingId, Long userId, Double amount) {
        // Check if payment already exists for this tracking ID
        if (paymentRepository.existsByTrackingId(trackingId)) {
            throw new ValidationException("Payment already initiated for this shipment");
        }
        
        // Validate amount
        if (amount == null || amount <= 0) {
            throw new ValidationException("Invalid payment amount");
        }
        
        // Create new payment
        Payment payment = new Payment(trackingId, userId, amount);
        payment.setPaymentMethod("ONLINE");
        payment.setPaymentStatus("PENDING");
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Complete a payment transaction
     * @param trackingId - Shipment tracking ID
     * @param paymentMethod - Payment method used (UPI, CARD, NET_BANKING)
     * @return Updated payment object with COMPLETED status
     */
    @Transactional
    public Payment completePayment(String trackingId, String paymentMethod) {
        // Find payment
        Payment payment = paymentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for tracking ID: " + trackingId));
        
        // Check if already completed
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            throw new ValidationException("Payment already completed for this shipment");
        }
        
        // Generate unique transaction ID
        String transactionId = generateTransactionId();
        
        // Update payment details
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "ONLINE");
        payment.setTransactionId(transactionId);
        payment.setCompletedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Get payment details by tracking ID
     * @param trackingId - Shipment tracking ID
     * @return Payment object
     */
    public Payment getPaymentByTrackingId(String trackingId) {
        return paymentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for tracking ID: " + trackingId));
    }
    
    /**
     * Get all payments for a user
     * @param userId - User ID
     * @return List of payments
     */
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    /**
     * Get all payments by status
     * @param status - Payment status (PENDING, COMPLETED, FAILED, REFUNDED)
     * @return List of payments
     */
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }
    
    /**
     * Check if payment is completed for a tracking ID
     * @param trackingId - Shipment tracking ID
     * @return true if payment is completed, false otherwise
     */
    public boolean isPaymentCompleted(String trackingId) {
        Payment payment = paymentRepository.findByTrackingId(trackingId).orElse(null);
        return payment != null && "COMPLETED".equals(payment.getPaymentStatus());
    }
    
    /**
     * Initiate refund for a payment
     * @param trackingId - Shipment tracking ID
     * @return Updated payment object with REFUNDED status
     */
    @Transactional
    public Payment refundPayment(String trackingId) {
        Payment payment = paymentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for tracking ID: " + trackingId));
        
        // Only completed payments can be refunded
        if (!"COMPLETED".equals(payment.getPaymentStatus())) {
            throw new ValidationException("Only completed payments can be refunded");
        }
        
        // Update status to REFUNDED
        payment.setPaymentStatus("REFUNDED");
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Get all completed payments
     * @return List of completed payments ordered by completion date
     */
    public List<Payment> getCompletedPayments() {
        return paymentRepository.findByPaymentStatusOrderByCompletedAtDesc("COMPLETED");
    }
    
    /**
     * Calculate total revenue from completed payments
     * @return Total revenue amount
     */
    public Double getTotalRevenue() {
        List<Payment> completedPayments = getCompletedPayments();
        return completedPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }
    
    /**
     * Generate unique transaction ID
     * @return Transaction ID in format: TXN{timestamp}{random}
     */
    private String generateTransactionId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN" + timestamp + randomPart;
    }
    
    /**
     * Fail a payment (for declined shipments or payment failures)
     * @param trackingId - Shipment tracking ID
     * @return Updated payment object with FAILED status
     */
    @Transactional
    public Payment failPayment(String trackingId) {
        Payment payment = paymentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for tracking ID: " + trackingId));
        
        payment.setPaymentStatus("FAILED");
        
        return paymentRepository.save(payment);
    }
}