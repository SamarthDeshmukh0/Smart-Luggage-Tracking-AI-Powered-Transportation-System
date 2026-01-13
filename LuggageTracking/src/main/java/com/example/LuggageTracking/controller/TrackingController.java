//package com.example.LuggageTracking.controller;
//
//import com.example.LuggageTracking.dto.GPSData;
//import com.example.LuggageTracking.model.GPSLog;
//import com.example.LuggageTracking.model.ShipmentStatus;
//import com.example.LuggageTracking.service.TrackingService;
//import com.example.LuggageTracking.service.ShipmentService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/tracking")
//@CrossOrigin(origins = "*")
//public class TrackingController {
//    
//    @Autowired
//    private TrackingService trackingService;
//    
//    @Autowired
//    private ShipmentService shipmentService;
//    
//    @PostMapping("/update")
//    public ResponseEntity<?> updateLocation(@Valid @RequestBody GPSData gpsData) {
//        try {
//            // Save GPS location
//            GPSLog log = trackingService.saveLocation(gpsData);
//
//            // Auto move shipment to IN_TRANSIT on first GPS ping
//            try {
//                var shipment =
//                        shipmentService.getShipmentByTrackingId(gpsData.getTrackingId());
//
//                if (shipment.getStatus() == ShipmentStatus.PENDING) {
//                    shipmentService.updateShipmentStatus(
//                            gpsData.getTrackingId(),
//                            ShipmentStatus.IN_TRANSIT
//                    );
//                }
//
//            } catch (Exception ignored) {}
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Location updated successfully",
//                    "anomalyDetected", log.getAnomalyDetected(),
//                    "anomalyMessage",
//                    log.getAnomalyMessage() != null ? log.getAnomalyMessage() : ""
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    
//    @GetMapping("/{trackingId}/live")
//    public ResponseEntity<?> getLiveLocation(@PathVariable String trackingId) {
//        try {
//            GPSLog log = trackingService.getLatestLocation(trackingId);
//            if (log == null) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.ok(log);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//    
//    @GetMapping("/{trackingId}/history")
//    public ResponseEntity<List<GPSLog>> getHistory(@PathVariable String trackingId) {
//        List<GPSLog> history = trackingService.getTrackingHistory(trackingId);
//        return ResponseEntity.ok(history);
//    }
//    
//    @GetMapping("/{trackingId}/anomalies")
//    public ResponseEntity<List<GPSLog>> getAnomalies(@PathVariable String trackingId) {
//        List<GPSLog> anomalies = trackingService.getAnomalies(trackingId);
//        return ResponseEntity.ok(anomalies);
//    }
//}
//



//from 2.3
package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.dto.GPSData;
import com.example.LuggageTracking.model.GPSLog;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.service.TrackingService;
import com.example.LuggageTracking.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class TrackingController {
    
    @Autowired
    private TrackingService trackingService;
    
    @Autowired
    private ShipmentService shipmentService;
    
    @PostMapping("/update")
    public ResponseEntity<?> updateLocation(@Valid @RequestBody GPSData gpsData) {
        try {
            // Save GPS location
            GPSLog log = trackingService.saveLocation(gpsData);

            // Auto move shipment to IN_TRANSIT on first GPS ping
            try {
                var shipment = shipmentService.getShipmentByTrackingId(gpsData.getTrackingId());

                if (shipment.getStatus() == ShipmentStatus.PENDING) {
                    shipmentService.updateShipmentStatus(
                            gpsData.getTrackingId(),
                            ShipmentStatus.IN_TRANSIT
                    );
                }
            } catch (Exception ignored) {}

            return ResponseEntity.ok(Map.of(
                    "message", "Location updated successfully",
                    "anomalyDetected", log.getAnomalyDetected(),
                    "anomalyMessage", log.getAnomalyMessage() != null ? log.getAnomalyMessage() : "",
                    "isComplete", log.getIsJourneyComplete() != null ? log.getIsJourneyComplete() : false,
                    "progress", log.getRouteProgressPercentage()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{trackingId}/live")
    public ResponseEntity<?> getLiveLocation(@PathVariable String trackingId) {
        try {
            GPSLog log = trackingService.getLatestLocation(trackingId);
            if (log == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(log);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{trackingId}/history")
    public ResponseEntity<List<GPSLog>> getHistory(@PathVariable String trackingId) {
        List<GPSLog> history = trackingService.getTrackingHistory(trackingId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/{trackingId}/anomalies")
    public ResponseEntity<List<GPSLog>> getAnomalies(@PathVariable String trackingId) {
        List<GPSLog> anomalies = trackingService.getAnomalies(trackingId);
        return ResponseEntity.ok(anomalies);
    }
    
    // NEW: Get tracking status with completion info
    @GetMapping("/{trackingId}/status")
    public ResponseEntity<?> getTrackingStatus(@PathVariable String trackingId) {
        try {
            Map<String, Object> status = trackingService.getTrackingStatus(trackingId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
