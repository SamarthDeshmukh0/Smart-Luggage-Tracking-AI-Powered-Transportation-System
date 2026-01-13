package com.example.LuggageTracking.dto;

public class ETAPredictionResponse {
    
    private Double predictedEtaMinutes;
    private String predictedEtaFormatted; // e.g., "2h 15m"
    private Double confidence;
    private String message;
    
    // Constructors
    public ETAPredictionResponse() {}
    
    public ETAPredictionResponse(Double predictedEtaMinutes) {
        this.predictedEtaMinutes = predictedEtaMinutes;
        this.predictedEtaFormatted = formatETA(predictedEtaMinutes);
    }
    
    public ETAPredictionResponse(Double predictedEtaMinutes, Double confidence) {
        this.predictedEtaMinutes = predictedEtaMinutes;
        this.confidence = confidence;
        this.predictedEtaFormatted = formatETA(predictedEtaMinutes);
    }
    
    // Helper method to format ETA
    private String formatETA(Double minutes) {
        if (minutes == null) return null;
        
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        
        if (hours == 0) {
            return mins + "m";
        } else if (mins == 0) {
            return hours + "h";
        } else {
            return hours + "h " + mins + "m";
        }
    }
    
    // Getters and Setters
    public Double getPredictedEtaMinutes() {
        return predictedEtaMinutes;
    }
    
    public void setPredictedEtaMinutes(Double predictedEtaMinutes) {
        this.predictedEtaMinutes = predictedEtaMinutes;
        this.predictedEtaFormatted = formatETA(predictedEtaMinutes);
    }
    
    public String getPredictedEtaFormatted() {
        return predictedEtaFormatted;
    }
    
    public void setPredictedEtaFormatted(String predictedEtaFormatted) {
        this.predictedEtaFormatted = predictedEtaFormatted;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
