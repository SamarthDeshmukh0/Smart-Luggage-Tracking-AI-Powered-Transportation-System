package com.example.LuggageTracking.dto;

public class AnomalyResponse {
    private Boolean anomaly;
    private String message;
    
    public AnomalyResponse() {}
    
    public AnomalyResponse(Boolean anomaly, String message) {
        this.anomaly = anomaly;
        this.message = message;
    }
    
    public Boolean getAnomaly() { return anomaly; }
    public void setAnomaly(Boolean anomaly) { this.anomaly = anomaly; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}