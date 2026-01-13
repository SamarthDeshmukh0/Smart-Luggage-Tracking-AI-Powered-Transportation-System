package com.example.LuggageTracking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_id", nullable = false, unique = true, length = 50)
    private String trackingId;
    
    @Column(name = "base_fare", nullable = false)
    private Double baseFare;
    
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;
    
    @Column(name = "distance_charge", nullable = false)
    private Double distanceCharge;
    
    @Column(name = "weight_charge", nullable = false)
    private Double weightCharge;
    
    @Column(name = "category_multiplier", nullable = false)
    private Double categoryMultiplier;
    
    @Column(name = "ai_predicted_cost", nullable = false)
    private Double aiPredictedCost;
    
    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Invoice() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTrackingId() {
        return trackingId;
    }
    
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    
    public Double getBaseFare() {
        return baseFare;
    }
    
    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Double getDistanceCharge() {
        return distanceCharge;
    }
    
    public void setDistanceCharge(Double distanceCharge) {
        this.distanceCharge = distanceCharge;
    }
    
    public Double getWeightCharge() {
        return weightCharge;
    }
    
    public void setWeightCharge(Double weightCharge) {
        this.weightCharge = weightCharge;
    }
    
    public Double getCategoryMultiplier() {
        return categoryMultiplier;
    }
    
    public void setCategoryMultiplier(Double categoryMultiplier) {
        this.categoryMultiplier = categoryMultiplier;
    }
    
    public Double getAiPredictedCost() {
        return aiPredictedCost;
    }
    
    public void setAiPredictedCost(Double aiPredictedCost) {
        this.aiPredictedCost = aiPredictedCost;
    }
    
    public Double getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}