//package com.example.LuggageTracking.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "gps_logs")
//public class GPSLog {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    
//    @Column(name = "tracking_id", nullable = false, length = 50)
//    private String trackingId;
//    
//    @Column(nullable = false)
//    private Double latitude;
//    
//    @Column(nullable = false)
//    private Double longitude;
//    
//    @Column(nullable = false)
//    private Double speed;
//    
//    @Column(nullable = false)
//    private LocalDateTime timestamp;
//    
//    @Column(name = "distance_remaining")
//    private Double distanceRemaining;
//    
//    @Column(name = "anomaly_detected")
//    private Boolean anomalyDetected = false;
//    
//    @Column(name = "anomaly_message")
//    private String anomalyMessage;
//    
//    // Constructors
//    public GPSLog() {}
//    
//    public GPSLog(String trackingId, Double latitude, Double longitude, Double speed) {
//        this.trackingId = trackingId;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.speed = speed;
//        this.timestamp = LocalDateTime.now();
//    }
//    
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//    
//    public void setId(Long id) {
//        this.id = id;
//    }
//    
//    public String getTrackingId() {
//        return trackingId;
//    }
//    
//    public void setTrackingId(String trackingId) {
//        this.trackingId = trackingId;
//    }
//    
//    public Double getLatitude() {
//        return latitude;
//    }
//    
//    public void setLatitude(Double latitude) {
//        this.latitude = latitude;
//    }
//    
//    public Double getLongitude() {
//        return longitude;
//    }
//    
//    public void setLongitude(Double longitude) {
//        this.longitude = longitude;
//    }
//    
//    public Double getSpeed() {
//        return speed;
//    }
//    
//    public void setSpeed(Double speed) {
//        this.speed = speed;
//    }
//    
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//    
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//    
//    public Double getDistanceRemaining() {
//        return distanceRemaining;
//    }
//    
//    public void setDistanceRemaining(Double distanceRemaining) {
//        this.distanceRemaining = distanceRemaining;
//    }
//    
//    public Boolean getAnomalyDetected() {
//        return anomalyDetected;
//    }
//    
//    public void setAnomalyDetected(Boolean anomalyDetected) {
//        this.anomalyDetected = anomalyDetected;
//    }
//    
//    public String getAnomalyMessage() {
//        return anomalyMessage;
//    }
//    
//    public void setAnomalyMessage(String anomalyMessage) {
//        this.anomalyMessage = anomalyMessage;
//    }
//}
//



//form 2.3
package com.example.LuggageTracking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gps_logs")
public class GPSLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_id", nullable = false, length = 50)
    private String trackingId;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private Double speed;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "distance_remaining")
    private Double distanceRemaining;
    
    @Column(name = "anomaly_detected")
    private Boolean anomalyDetected = false;
    
    @Column(name = "anomaly_message")
    private String anomalyMessage;
    
    // NEW FIELDS FOR PERSISTENT TRACKING
    @Column(name = "route_progress_percentage")
    private Double routeProgressPercentage = 0.0;
    
    @Column(name = "is_journey_complete")
    private Boolean isJourneyComplete = false;
    
    @Column(name = "current_route_index")
    private Integer currentRouteIndex = 0;
    
    @Column(name = "estimated_total_distance")
    private Double estimatedTotalDistance;
    
    // Constructors
    public GPSLog() {}
    
    public GPSLog(String trackingId, Double latitude, Double longitude, Double speed) {
        this.trackingId = trackingId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = LocalDateTime.now();
    }
    
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
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getSpeed() {
        return speed;
    }
    
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Double getDistanceRemaining() {
        return distanceRemaining;
    }
    
    public void setDistanceRemaining(Double distanceRemaining) {
        this.distanceRemaining = distanceRemaining;
    }
    
    public Boolean getAnomalyDetected() {
        return anomalyDetected;
    }
    
    public void setAnomalyDetected(Boolean anomalyDetected) {
        this.anomalyDetected = anomalyDetected;
    }
    
    public String getAnomalyMessage() {
        return anomalyMessage;
    }
    
    public void setAnomalyMessage(String anomalyMessage) {
        this.anomalyMessage = anomalyMessage;
    }
    
    // NEW GETTERS/SETTERS
    public Double getRouteProgressPercentage() {
        return routeProgressPercentage;
    }
    
    public void setRouteProgressPercentage(Double routeProgressPercentage) {
        this.routeProgressPercentage = routeProgressPercentage;
    }
    
    public Boolean getIsJourneyComplete() {
        return isJourneyComplete;
    }
    
    public void setIsJourneyComplete(Boolean isJourneyComplete) {
        this.isJourneyComplete = isJourneyComplete;
    }
    
    public Integer getCurrentRouteIndex() {
        return currentRouteIndex;
    }
    
    public void setCurrentRouteIndex(Integer currentRouteIndex) {
        this.currentRouteIndex = currentRouteIndex;
    }
    
    public Double getEstimatedTotalDistance() {
        return estimatedTotalDistance;
    }
    
    public void setEstimatedTotalDistance(Double estimatedTotalDistance) {
        this.estimatedTotalDistance = estimatedTotalDistance;
    }
}
