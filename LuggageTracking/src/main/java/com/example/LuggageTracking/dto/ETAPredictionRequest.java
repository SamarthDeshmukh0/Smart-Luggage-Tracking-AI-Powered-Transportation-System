package com.example.LuggageTracking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ETAPredictionRequest {
    
    @NotNull(message = "Distance remaining is required")
    @Positive(message = "Distance remaining must be positive")
    private Double distanceRemaining;
    
    @NotNull(message = "Average speed is required")
    @Positive(message = "Average speed must be positive")
    private Double avgSpeed;
    
    @NotNull(message = "Traffic factor is required")
    private Double trafficFactor;
    
    private String trackingId;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    
    // Constructors
    public ETAPredictionRequest() {}
    
    public ETAPredictionRequest(Double distanceRemaining, Double avgSpeed, Double trafficFactor) {
        this.distanceRemaining = distanceRemaining;
        this.avgSpeed = avgSpeed;
        this.trafficFactor = trafficFactor;
    }
    
    // Getters and Setters
    public Double getDistanceRemaining() {
        return distanceRemaining;
    }
    
    public void setDistanceRemaining(Double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }
    
    public Double getAvgSpeed() {
        return avgSpeed;
    }
    
    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
    
    public Double getTrafficFactor() {
        return trafficFactor;
    }
    
    public void setTrafficFactor(Double trafficFactor) {
        this.trafficFactor = trafficFactor;
    }
    
    public String getTrackingId() {
        return trackingId;
    }
    
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public Double getDestinationLatitude() {
        return destinationLatitude;
    }
    
    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }
    
    public Double getDestinationLongitude() {
        return destinationLongitude;
    }
    
    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
}