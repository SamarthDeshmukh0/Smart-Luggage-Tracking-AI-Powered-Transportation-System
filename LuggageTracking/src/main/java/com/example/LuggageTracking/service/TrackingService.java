////version2 adding of getService..()meth
//
//package com.example.LuggageTracking.service;
//
//import com.example.LuggageTracking.dto.GPSData;
//import com.example.LuggageTracking.model.GPSLog;
//import com.example.LuggageTracking.model.Shipment;
//import com.example.LuggageTracking.model.ShipmentStatus;
//import com.example.LuggageTracking.repository.GPSLogRepository;
//import com.example.LuggageTracking.util.DistanceCalculator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class TrackingService {
//    
//    @Autowired
//    private GPSLogRepository gpsLogRepository;
//    
//    @Autowired
//    private AlertService alertService;
//    
//    @Autowired
//    private DistanceCalculator distanceCalculator;
//    
//    @Autowired
//    private ShipmentService shipmentService;
//    
//    @Transactional
//    public GPSLog saveLocation(GPSData gpsData) {
//        GPSLog log = new GPSLog();
//        log.setTrackingId(gpsData.getTrackingId());
//        log.setLatitude(gpsData.getLatitude());
//        log.setLongitude(gpsData.getLongitude());
//        log.setSpeed(gpsData.getSpeed());
//        log.setTimestamp(gpsData.getTimestamp() != null ? gpsData.getTimestamp() : LocalDateTime.now());
//        log.setDistanceRemaining(gpsData.getDistanceRemaining());
//        
//        // Calculate and store progress
//        GPSLog previousLog = gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(log.getTrackingId());
//        
//        if (previousLog != null && previousLog.getEstimatedTotalDistance() != null) {
//            // Continue tracking with existing total distance
//            log.setEstimatedTotalDistance(previousLog.getEstimatedTotalDistance());
//            
//            if (log.getDistanceRemaining() != null && log.getEstimatedTotalDistance() > 0) {
//                double progressPct = ((log.getEstimatedTotalDistance() - log.getDistanceRemaining()) 
//                                     / log.getEstimatedTotalDistance()) * 100.0;
//                log.setRouteProgressPercentage(Math.min(100.0, Math.max(0.0, progressPct)));
//            } else {
//                log.setRouteProgressPercentage(previousLog.getRouteProgressPercentage());
//            }
//            
//            // Increment route index
//            Integer prevIndex = previousLog.getCurrentRouteIndex();
//            log.setCurrentRouteIndex(prevIndex != null ? prevIndex + 1 : 1);
//            
//            // Check if journey is complete (within 100m of destination)
//            if (log.getDistanceRemaining() != null && log.getDistanceRemaining() < 0.1) {
//                log.setIsJourneyComplete(true);
//                log.setRouteProgressPercentage(100.0);
//                
//                // Mark shipment as delivered
//                try {
//                    Shipment shipment = shipmentService.getShipmentByTrackingId(log.getTrackingId());
//                    if (shipment.getStatus() == ShipmentStatus.IN_TRANSIT) {
//                        shipmentService.updateShipmentStatus(log.getTrackingId(), ShipmentStatus.DELIVERED);
//                    }
//                } catch (Exception e) {
//                    System.err.println("Error updating shipment status: " + e.getMessage());
//                }
//            } else {
//                log.setIsJourneyComplete(false);
//            }
//        } else {
//            // First GPS log - initialize
//            log.setEstimatedTotalDistance(gpsData.getDistanceRemaining());
//            log.setRouteProgressPercentage(0.0);
//            log.setCurrentRouteIndex(0);
//            log.setIsJourneyComplete(false);
//        }
//        
//        // Check for anomalies
//        checkForAnomalies(log);
//        
//        return gpsLogRepository.save(log);
//    }
//    
//    private void checkForAnomalies(GPSLog log) {
//        // Check if luggage is not moving
//        if (log.getSpeed() < 5.0) {
//            log.setAnomalyDetected(true);
//            log.setAnomalyMessage("Luggage not moving or moving very slowly");
//            alertService.createAlert(log.getTrackingId(), "Luggage not moving", "MEDIUM");
//        }
//        
//        // Check for abnormal speed
//        if (log.getSpeed() > 120.0) {
//            log.setAnomalyDetected(true);
//            log.setAnomalyMessage("Abnormal speed detected");
//            alertService.createAlert(log.getTrackingId(), "Abnormal speed: " + log.getSpeed() + " km/h", "HIGH");
//        }
//        
//        // Check for route deviation
//        GPSLog previousLog = gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(log.getTrackingId());
//        if (previousLog != null) {
//            double distance = distanceCalculator.calculateDistance(
//                previousLog.getLatitude(), previousLog.getLongitude(),
//                log.getLatitude(), log.getLongitude()
//            );
//            
//            // If moved too far in short time, might be route deviation
//            if (distance > 50.0) {
//                log.setAnomalyDetected(true);
//                log.setAnomalyMessage("Possible route deviation detected");
//                alertService.createAlert(log.getTrackingId(), "Route deviation detected", "HIGH");
//            }
//        }
//    }
//    
//    public GPSLog getLatestLocation(String trackingId) {
//        return gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(trackingId);
//    }
//    
//    public List<GPSLog> getTrackingHistory(String trackingId) {
//        return gpsLogRepository.findByTrackingIdOrderByTimestampDesc(trackingId);
//    }
//    
//    public List<GPSLog> getAnomalies(String trackingId) {
//        return gpsLogRepository.findByTrackingIdAndAnomalyDetected(trackingId, true);
//    }
//    
//    // Get tracking status with completion info
//    public Map<String, Object> getTrackingStatus(String trackingId) {
//        GPSLog latestLog = getLatestLocation(trackingId);
//        Map<String, Object> status = new HashMap<>();
//        
//        if (latestLog != null) {
//            status.put("isComplete", latestLog.getIsJourneyComplete() != null ? latestLog.getIsJourneyComplete() : false);
//            status.put("progress", latestLog.getRouteProgressPercentage() != null ? latestLog.getRouteProgressPercentage() : 0.0);
//            status.put("currentIndex", latestLog.getCurrentRouteIndex() != null ? latestLog.getCurrentRouteIndex() : 0);
//            status.put("latitude", latestLog.getLatitude());
//            status.put("longitude", latestLog.getLongitude());
//            status.put("speed", latestLog.getSpeed());
//            status.put("distanceRemaining", latestLog.getDistanceRemaining());
//            status.put("timestamp", latestLog.getTimestamp());
//        } else {
//            status.put("isComplete", false);
//            status.put("progress", 0.0);
//            status.put("currentIndex", 0);
//            status.put("latitude", null);
//            status.put("longitude", null);
//            status.put("speed", 0.0);
//            status.put("distanceRemaining", null);
//            status.put("timestamp", null);
//        }
//        
//        return status;
//    }
//}


//from 2.3

package com.example.LuggageTracking.service;

import com.example.LuggageTracking.dto.GPSData;
import com.example.LuggageTracking.model.GPSLog;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.repository.GPSLogRepository;
import com.example.LuggageTracking.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrackingService {
    
    @Autowired
    private GPSLogRepository gpsLogRepository;
    
    @Autowired
    private AlertService alertService;
    
    @Autowired
    private DistanceCalculator distanceCalculator;
    
    @Autowired
    private ShipmentService shipmentService;
    
    @Autowired
    private BillingService billingService;
    
    @Transactional
    public GPSLog saveLocation(GPSData gpsData) {
        GPSLog log = new GPSLog();
        log.setTrackingId(gpsData.getTrackingId());
        log.setLatitude(gpsData.getLatitude());
        log.setLongitude(gpsData.getLongitude());
        log.setSpeed(gpsData.getSpeed());
        log.setTimestamp(gpsData.getTimestamp() != null ? gpsData.getTimestamp() : LocalDateTime.now());
        log.setDistanceRemaining(gpsData.getDistanceRemaining());
        
        // Calculate and store progress
        GPSLog previousLog = gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(log.getTrackingId());
        
        if (previousLog != null && previousLog.getEstimatedTotalDistance() != null) {
            // Continue tracking with existing total distance
            log.setEstimatedTotalDistance(previousLog.getEstimatedTotalDistance());
            
            if (log.getDistanceRemaining() != null && log.getEstimatedTotalDistance() > 0) {
                double progressPct = ((log.getEstimatedTotalDistance() - log.getDistanceRemaining()) 
                                     / log.getEstimatedTotalDistance()) * 100.0;
                log.setRouteProgressPercentage(Math.min(100.0, Math.max(0.0, progressPct)));
            } else {
                log.setRouteProgressPercentage(previousLog.getRouteProgressPercentage());
            }
            
            // Increment route index
            Integer prevIndex = previousLog.getCurrentRouteIndex();
            log.setCurrentRouteIndex(prevIndex != null ? prevIndex + 1 : 1);
            
            // Check if journey is complete (within 100m of destination)
            if (log.getDistanceRemaining() != null && log.getDistanceRemaining() < 0.1) {
                log.setIsJourneyComplete(true);
                log.setRouteProgressPercentage(100.0);
                
                // Mark shipment as delivered and generate invoice
                try {
                    Shipment shipment = shipmentService.getShipmentByTrackingId(log.getTrackingId());
                    if (shipment.getStatus() == ShipmentStatus.IN_TRANSIT) {
                        shipmentService.updateShipmentStatus(log.getTrackingId(), ShipmentStatus.DELIVERED);
                        
                        // Auto-generate invoice
                        try {
                            billingService.generateInvoice(log.getTrackingId());
                        } catch (Exception e) {
                            System.err.println("Error generating invoice: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error updating shipment status: " + e.getMessage());
                }
            } else {
                log.setIsJourneyComplete(false);
            }
        } else {
            // First GPS log - initialize
            log.setEstimatedTotalDistance(gpsData.getDistanceRemaining());
            log.setRouteProgressPercentage(0.0);
            log.setCurrentRouteIndex(0);
            log.setIsJourneyComplete(false);
        }
        
        // Check for anomalies
        checkForAnomalies(log);
        
        return gpsLogRepository.save(log);
    }
    
    private void checkForAnomalies(GPSLog log) {
        // Check if luggage is not moving
        if (log.getSpeed() < 5.0) {
            log.setAnomalyDetected(true);
            log.setAnomalyMessage("Luggage not moving or moving very slowly");
            alertService.createAlert(log.getTrackingId(), "Luggage not moving", "MEDIUM");
        }
        
        // Check for abnormal speed
        if (log.getSpeed() > 120.0) {
            log.setAnomalyDetected(true);
            log.setAnomalyMessage("Abnormal speed detected");
            alertService.createAlert(log.getTrackingId(), "Abnormal speed: " + log.getSpeed() + " km/h", "HIGH");
        }
        
        // Check for route deviation
        GPSLog previousLog = gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(log.getTrackingId());
        if (previousLog != null) {
            double distance = distanceCalculator.calculateDistance(
                previousLog.getLatitude(), previousLog.getLongitude(),
                log.getLatitude(), log.getLongitude()
            );
            
            // If moved too far in short time, might be route deviation
            if (distance > 50.0) {
                log.setAnomalyDetected(true);
                log.setAnomalyMessage("Possible route deviation detected");
                alertService.createAlert(log.getTrackingId(), "Route deviation detected", "HIGH");
            }
        }
    }
    
    public GPSLog getLatestLocation(String trackingId) {
        return gpsLogRepository.findFirstByTrackingIdOrderByTimestampDesc(trackingId);
    }
    
    public List<GPSLog> getTrackingHistory(String trackingId) {
        return gpsLogRepository.findByTrackingIdOrderByTimestampDesc(trackingId);
    }
    
    public List<GPSLog> getAnomalies(String trackingId) {
        return gpsLogRepository.findByTrackingIdAndAnomalyDetected(trackingId, true);
    }
    
    // Get tracking status with completion info
    public Map<String, Object> getTrackingStatus(String trackingId) {
        GPSLog latestLog = getLatestLocation(trackingId);
        Map<String, Object> status = new HashMap<>();
        
        if (latestLog != null) {
            status.put("isComplete", latestLog.getIsJourneyComplete() != null ? latestLog.getIsJourneyComplete() : false);
            status.put("progress", latestLog.getRouteProgressPercentage() != null ? latestLog.getRouteProgressPercentage() : 0.0);
            status.put("currentIndex", latestLog.getCurrentRouteIndex() != null ? latestLog.getCurrentRouteIndex() : 0);
            status.put("latitude", latestLog.getLatitude());
            status.put("longitude", latestLog.getLongitude());
            status.put("speed", latestLog.getSpeed());
            status.put("distanceRemaining", latestLog.getDistanceRemaining());
            status.put("timestamp", latestLog.getTimestamp());
        } else {
            status.put("isComplete", false);
            status.put("progress", 0.0);
            status.put("currentIndex", 0);
            status.put("latitude", null);
            status.put("longitude", null);
            status.put("speed", 0.0);
            status.put("distanceRemaining", null);
            status.put("timestamp", null);
        }
        
        return status;
    }
}
