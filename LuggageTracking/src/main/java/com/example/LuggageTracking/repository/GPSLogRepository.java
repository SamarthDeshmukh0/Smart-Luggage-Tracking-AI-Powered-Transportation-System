package com.example.LuggageTracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LuggageTracking.model.GPSLog;

import java.util.List;

@Repository
public interface GPSLogRepository extends JpaRepository<GPSLog, Long> {
    List<GPSLog> findByTrackingIdOrderByTimestampDesc(String trackingId);
    GPSLog findFirstByTrackingIdOrderByTimestampDesc(String trackingId);
    List<GPSLog> findByTrackingIdAndAnomalyDetected(String trackingId, Boolean anomalyDetected);
}
