package com.example.LuggageTracking.service;

import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.model.Alert;
import com.example.LuggageTracking.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Transactional
    public Alert createAlert(String trackingId, String message, String severity) {
        Alert alert = new Alert(trackingId, message, severity);
        return alertRepository.save(alert);
    }
    
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByIsResolvedOrderByTimestampDesc(false);
    }
    
    public List<Alert> getAlertsByTrackingId(String trackingId) {
        return alertRepository.findByTrackingId(trackingId);
    }
    
    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverityAndIsResolved(severity, false);
    }
    
    @Transactional
    public Alert resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }
}
