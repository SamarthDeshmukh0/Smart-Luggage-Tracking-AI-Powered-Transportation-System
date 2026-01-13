package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.model.Alert;
import com.example.LuggageTracking.model.Payment;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.repository.InvoiceRepository;
import com.example.LuggageTracking.service.AlertService;
import com.example.LuggageTracking.service.PaymentService;
import com.example.LuggageTracking.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private AlertService alertService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private InvoiceRepository invoiceRepository;

    // ---------------- DASHBOARD STATS ----------------
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Shipment> allShipments = shipmentService.getAllShipments();
        List<Alert> activeAlerts = alertService.getActiveAlerts();

        long total = allShipments.size();
        long active = allShipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.IN_TRANSIT)
                .count();

        long completed = allShipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED)
                .count();

        long pending = allShipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.PENDING)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("completed", completed);
        stats.put("pending", pending);
        stats.put("anomalies", activeAlerts.size());

        return ResponseEntity.ok(stats);
    }

    // ---------------- ACTIVE ALERTS ----------------
    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    // ---------------- RESOLVE ALERT ----------------
    @PostMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long alertId) {
        try {
            Alert alert = alertService.resolveAlert(alertId);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------------- ACTIVE SHIPMENTS ----------------
    @GetMapping("/active-shipments")
    public ResponseEntity<List<Shipment>> getActiveShipments() {
        return ResponseEntity.ok(shipmentService.getActiveShipments());
    }

    // ---------------- BASIC ANALYTICS (OLD) ----------------
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        List<Shipment> shipments = shipmentService.getAllShipments();

        double totalRevenue = invoiceRepository.getTotalRevenue() != null
                ? invoiceRepository.getTotalRevenue()
                : 0.0;

        long pending = shipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.PENDING)
                .count();

        long inTransit = shipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.IN_TRANSIT)
                .count();

        long delivered = shipments.stream()
                .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED)
                .count();

        Map<String, Long> statusDistribution = Map.of(
                "PENDING", pending,
                "IN_TRANSIT", inTransit,
                "DELIVERED", delivered
        );

        Map<String, Long> categoryDistribution = shipments.stream()
                .collect(Collectors.groupingBy(
                        Shipment::getCategory,
                        Collectors.counting()
                ));

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRevenue", totalRevenue);
        analytics.put("totalShipments", shipments.size());
        analytics.put("statusDistribution", statusDistribution);
        analytics.put("categoryDistribution", categoryDistribution);

        return ResponseEntity.ok(analytics);
    }

    // ---------------- ACCEPT SHIPMENT ----------------
    @PostMapping("/shipments/{trackingId}/accept")
    public ResponseEntity<?> acceptShipment(@PathVariable String trackingId) {
        try {
            // Check if payment is completed
            if (!paymentService.isPaymentCompleted(trackingId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Payment not completed for this shipment"));
            }

            // Update status to IN_TRANSIT
            Shipment shipment = shipmentService.updateShipmentStatus(
                    trackingId, 
                    ShipmentStatus.IN_TRANSIT
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Shipment accepted and started",
                    "shipment", shipment,
                    "status", "IN_TRANSIT"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------------- DECLINE SHIPMENT ----------------
    @PostMapping("/shipments/{trackingId}/decline")
    public ResponseEntity<?> declineShipment(
            @PathVariable String trackingId,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            
            // Mark shipment as CANCELLED
            Shipment shipment = shipmentService.updateShipmentStatus(
                    trackingId,
                    ShipmentStatus.CANCELLED
            );

            // Initiate refund
            Payment payment = paymentService.refundPayment(trackingId);

            return ResponseEntity.ok(Map.of(
                    "message", "Shipment declined. Refund initiated.",
                    "shipment", shipment,
                    "status", "CANCELLED",
                    "reason", reason != null ? reason : "Declined by admin",
                    "refundAmount", payment.getAmount()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------------- REAL-TIME ANALYTICS ----------------
    @GetMapping("/analytics/real")
    public ResponseEntity<Map<String, Object>> getRealAnalytics() {
        try {
            List<Shipment> allShipments = shipmentService.getAllShipments();
            
            // Filter only paid shipments
            List<Shipment> paidShipments = allShipments.stream()
                    .filter(s -> paymentService.isPaymentCompleted(s.getTrackingId()))
                    .collect(Collectors.toList());

            Map<String, Object> analytics = new HashMap<>();

            // 1. Total Revenue (from completed payments only)
            Double totalRevenue = paidShipments.stream()
                    .map(Shipment::getPredictedCost)
                    .reduce(0.0, Double::sum);

            // 2. Shipments by Status
            Map<ShipmentStatus, Long> shipmentsByStatus = paidShipments.stream()
                    .collect(Collectors.groupingBy(
                            Shipment::getStatus,
                            Collectors.counting()
                    ));

            // 3. Category Distribution
            Map<String, Long> categoryDistribution = paidShipments.stream()
                    .collect(Collectors.groupingBy(
                            Shipment::getCategory,
                            Collectors.counting()
                    ));

            // 4. Average Cost per Category
            Map<String, Double> avgCostByCategory = paidShipments.stream()
                    .collect(Collectors.groupingBy(
                            Shipment::getCategory,
                            Collectors.averagingDouble(Shipment::getPredictedCost)
                    ));

            // 5. Daily Shipments (Last 7 days)
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            Map<String, Long> dailyShipments = paidShipments.stream()
                    .filter(s -> s.getCreatedAt().isAfter(sevenDaysAgo))
                    .collect(Collectors.groupingBy(
                            s -> s.getCreatedAt().toLocalDate().toString(),
                            Collectors.counting()
                    ));

            // 6. Top Routes
            Map<String, Long> topRoutes = paidShipments.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getSource() + " → " + s.getDestination(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            // 7. Monthly Revenue (Current month)
            Double monthlyRevenue = paidShipments.stream()
                    .filter(s -> s.getCreatedAt().getMonth() == LocalDateTime.now().getMonth())
                    .mapToDouble(Shipment::getPredictedCost)
                    .sum();

            // 8. Average Delivery Time (for delivered shipments)
            Double avgDeliveryTime = paidShipments.stream()
                    .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED && s.getDeliveredAt() != null)
                    .mapToDouble(s -> {
                        long minutes = ChronoUnit.MINUTES.between(s.getCreatedAt(), s.getDeliveredAt());
                        return minutes / 60.0;
                    })
                    .average()
                    .orElse(0.0);

            // 9. Success Rate
            long totalCount = paidShipments.size();
            long deliveredCount = paidShipments.stream()
                    .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED)
                    .count();
            Double successRate = totalCount > 0 ? (deliveredCount * 100.0 / totalCount) : 0.0;

            // Build response
            analytics.put("totalRevenue", totalRevenue);
            analytics.put("monthlyRevenue", monthlyRevenue);
            analytics.put("totalShipments", totalCount);
            analytics.put("shipmentsByStatus", shipmentsByStatus);
            analytics.put("categoryDistribution", categoryDistribution);
            analytics.put("avgCostByCategory", avgCostByCategory);
            analytics.put("dailyShipments", dailyShipments);
            analytics.put("topRoutes", topRoutes);
            analytics.put("avgDeliveryTimeHours", avgDeliveryTime);
            analytics.put("successRate", successRate);
            analytics.put("pendingShipments", shipmentsByStatus.getOrDefault(ShipmentStatus.PENDING, 0L));
            analytics.put("inTransitShipments", shipmentsByStatus.getOrDefault(ShipmentStatus.IN_TRANSIT, 0L));
            analytics.put("deliveredShipments", deliveredCount);
            analytics.put("cancelledShipments", shipmentsByStatus.getOrDefault(ShipmentStatus.CANCELLED, 0L));

            return ResponseEntity.ok(analytics);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
} 
