package com.example.LuggageTracking.service;

import com.example.LuggageTracking.dto.CostPredictionRequest;
import com.example.LuggageTracking.dto.CostPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class MLService {
    
    @Value("${ml.service.url}")
    private String mlServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public CostPredictionResponse predictCost(CostPredictionRequest request) {
        try {
            String url = mlServiceUrl + "/predict/cost";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CostPredictionRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Double predictedCost = ((Number) response.getBody().get("predicted_cost")).doubleValue();
                return new CostPredictionResponse(predictedCost);
            }
        } catch (Exception e) {
            System.err.println("ML Service call failed: " + e.getMessage());
        }
        
        // Fallback calculation if ML service fails
        return new CostPredictionResponse(calculateFallbackCost(request));
    }
    
    public Map<String, Object> predictETA(Map<String, Object> request) {
        try {
            String url = mlServiceUrl + "/predict/eta";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("ETA prediction failed: " + e.getMessage());
        }
        
        // Fallback ETA calculation
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("predicted_eta_minutes", 120.0);
        return fallback;
    }
    
    private Double calculateFallbackCost(CostPredictionRequest request) {
        double baseCost = 50.0;
        double distanceCost = request.getDistanceKm() * 10.0;
        double weightCost = request.getWeightKg() * 20.0;
        
        double multiplier = 1.0;
        if ("fragile".equalsIgnoreCase(request.getLuggageType())) {
            multiplier = 1.2;
        } else if ("express".equalsIgnoreCase(request.getLuggageType())) {
            multiplier = 1.5;
        }
        
        return (baseCost + distanceCost + weightCost) * multiplier;
    }
}
