package com.example.LuggageTracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LuggageTracking.model.Alert;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByTrackingId(String trackingId);
    List<Alert> findByIsResolved(Boolean isResolved);
    List<Alert> findByIsResolvedOrderByTimestampDesc(Boolean isResolved);
    List<Alert> findBySeverityAndIsResolved(String severity, Boolean isResolved);
}
