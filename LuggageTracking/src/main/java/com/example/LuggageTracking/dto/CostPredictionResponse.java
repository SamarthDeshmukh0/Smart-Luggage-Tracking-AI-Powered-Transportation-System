package com.example.LuggageTracking.dto;

	public class CostPredictionResponse {
	    private Double predictedCost;
	    
	    public CostPredictionResponse() {}
	    
	    public CostPredictionResponse(Double predictedCost) {
	        this.predictedCost = predictedCost;
	    }
	    
	    public Double getPredictedCost() { return predictedCost; }
	    public void setPredictedCost(Double predictedCost) { this.predictedCost = predictedCost; }
	}