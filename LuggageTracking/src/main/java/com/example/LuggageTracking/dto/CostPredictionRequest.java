package com.example.LuggageTracking.dto;


	public class CostPredictionRequest {
	    private Double distanceKm;
	    private Double weightKg;
	    private String luggageType;
	    private Double speedPattern;
	    private Double trafficFactor;
	    
	    public CostPredictionRequest() {}
	    
	    // Getters and Setters
	    public Double getDistanceKm() { return distanceKm; }
	    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
	    public Double getWeightKg() { return weightKg; }
	    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
	    public String getLuggageType() { return luggageType; }
	    public void setLuggageType(String luggageType) { this.luggageType = luggageType; }
	    public Double getSpeedPattern() { return speedPattern; }
	    public void setSpeedPattern(Double speedPattern) { this.speedPattern = speedPattern; }
	    public Double getTrafficFactor() { return trafficFactor; }
	    public void setTrafficFactor(Double trafficFactor) { this.trafficFactor = trafficFactor; }
	}

	