//package com.example.LuggageTracking.controller;
//
//import com.example.LuggageTracking.dto.CostPredictionRequest;
//import com.example.LuggageTracking.dto.CostPredictionResponse;
//import com.example.LuggageTracking.exception.ResourceNotFoundException;
//import com.example.LuggageTracking.model.Shipment;
//import com.example.LuggageTracking.model.User;
//import com.example.LuggageTracking.security.JwtUtil;
//import com.example.LuggageTracking.service.MLService;
//import com.example.LuggageTracking.service.S3Service;
//import com.example.LuggageTracking.service.ShipmentService;
//import com.example.LuggageTracking.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/shipments")
//@CrossOrigin(origins = "*")
//public class ShipmentController {
//    
//    @Autowired
//    private ShipmentService shipmentService;
//    
//    @Autowired
//    private UserService userService;
//    
//    @Autowired
//    private S3Service s3Service;
//    
//    @Autowired
//    private MLService mlService;
//    
//    @Autowired
//    private JwtUtil jwtUtil;
//    
//    @PostMapping("/create")
//    public ResponseEntity<?> createShipment(
//            @RequestParam("image") MultipartFile image,
//            @RequestParam("source") String source,
//            @RequestParam("destination") String destination,
//            @RequestParam("weight") Double weight,
//            @RequestParam("category") String category,
//            @RequestHeader("Authorization") String token) {
//        try {
//            // Extract user from token
//            String jwt = token.substring(7);
//            String username = jwtUtil.extractUsername(jwt);
//            User user = userService.findByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//            
//            // Upload image to S3
//            String imageUrl = s3Service.uploadFile(image, "luggage-images");
//            
//            // Get ML cost prediction
//            CostPredictionRequest costRequest = new CostPredictionRequest();
//            costRequest.setDistanceKm(150.0); // You can calculate actual distance
//            costRequest.setWeightKg(weight);
//            costRequest.setLuggageType(category);
//            costRequest.setSpeedPattern(50.0);
//            costRequest.setTrafficFactor(1.0);
//            
//            CostPredictionResponse costPrediction = mlService.predictCost(costRequest);
//            
//            // Create shipment
//            Shipment shipment = new Shipment();
//            shipment.setUserId(user.getId());
//            shipment.setImageUrl(imageUrl);
//            shipment.setWeight(weight);
//            shipment.setCategory(category);
//            shipment.setSource(source);
//            shipment.setDestination(destination);
//            shipment.setPredictedCost(costPrediction.getPredictedCost());
//            
//            Shipment savedShipment = shipmentService.createShipment(shipment);
//            
//            return ResponseEntity.ok(savedShipment);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//    
//    @GetMapping("/my")
//    public ResponseEntity<List<Shipment>> getMyShipments(@RequestHeader("Authorization") String token) {
//        try {
//            String jwt = token.substring(7);
//            String username = jwtUtil.extractUsername(jwt);
//            User user = userService.findByUsername(username)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//            
//            List<Shipment> shipments = shipmentService.getShipmentsByUserId(user.getId());
//            return ResponseEntity.ok(shipments);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//    
//    @GetMapping("/{trackingId}")
//    public ResponseEntity<Shipment> getShipment(@PathVariable String trackingId) {
//        try {
//            Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
//            return ResponseEntity.ok(shipment);
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//    
//    @GetMapping("/admin/all")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<List<Shipment>> getAllShipments() {
//        List<Shipment> shipments = shipmentService.getAllShipments();
//        return ResponseEntity.ok(shipments);
//    }
//    
//    @PutMapping("/{trackingId}/status")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<Shipment> updateStatus(
//            @PathVariable String trackingId,
//            @RequestBody Map<String, String> request) {
//        try {
//            String status = request.get("status");
//            Shipment shipment = shipmentService.updateShipmentStatus(trackingId, status);
//            return ResponseEntity.ok(shipment);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//}
//


//v2
//package com.example.LuggageTracking.controller;
//
//import com.example.LuggageTracking.dto.CostPredictionRequest;
//import com.example.LuggageTracking.model.ShipmentStatus;
//import com.example.LuggageTracking.dto.CostPredictionResponse;
//import com.example.LuggageTracking.exception.ResourceNotFoundException;
//import com.example.LuggageTracking.model.Shipment;
//import com.example.LuggageTracking.model.User;
//import com.example.LuggageTracking.security.JwtUtil;
//import com.example.LuggageTracking.service.MLService;
//import com.example.LuggageTracking.service.S3Service;
//import com.example.LuggageTracking.service.ShipmentService;
//import com.example.LuggageTracking.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/shipments")
//@CrossOrigin(origins = "*")
//public class ShipmentController {
//
//    @Autowired
//    private ShipmentService shipmentService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private S3Service s3Service;
//
//    @Autowired
//    private MLService mlService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // Create a new shipment
////    @PostMapping("/create")
////    public ResponseEntity<?> createShipment(
////            @RequestParam("image") MultipartFile image,
////            @RequestParam("source") String source,
////            @RequestParam("destination") String destination,
////            @RequestParam("weight") Double weight,
////            @RequestParam("category") String category,
////            @RequestHeader("Authorization") String token) {
////        try {
////            // Extract email from JWT
////            String jwt = token.substring(7); // remove "Bearer "
////            String email = jwtUtil.extractUsername(jwt); // token contains email
////            User user = userService.findByEmail(email)
////                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
////
////            // Upload image to S3
////            String imageUrl = s3Service.uploadFile(image, "luggage-images");
////
////            // Prepare ML cost prediction
////            CostPredictionRequest costRequest = new CostPredictionRequest();
////            costRequest.setDistanceKm(150.0); // optionally calculate real distance
////            costRequest.setWeightKg(weight);
////            costRequest.setLuggageType(category);
////            costRequest.setSpeedPattern(50.0);
////            costRequest.setTrafficFactor(1.0);
////
////            CostPredictionResponse costPrediction = mlService.predictCost(costRequest);
////
////            // Create shipment
////            Shipment shipment = new Shipment();
////            shipment.setUserId(user.getId());
////            shipment.setImageUrl(imageUrl);
////            shipment.setWeight(weight);
////            shipment.setCategory(category);
////            shipment.setSource(source);
////            shipment.setDestination(destination);
////            shipment.setPredictedCost(costPrediction.getPredictedCost());
////
////            Shipment savedShipment = shipmentService.createShipment(shipment);
////
////            return ResponseEntity.ok(savedShipment);
////
////        } catch (Exception e) {
////            e.printStackTrace(); // log server-side
////            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
////        }
////    }
//    	
//    
//    //just skipping the aws
//    
//    @PostMapping("/create")
//    public ResponseEntity<?> createShipment(
//            @RequestParam("image") MultipartFile image,
//            @RequestParam("source") String source,
//            @RequestParam("destination") String destination,
//            @RequestParam("weight") Double weight,
//            @RequestParam("category") String category,
//            @RequestHeader("Authorization") String token) {
//        try {
//            // Extract JWT token and username
//            String jwt = token.substring(7);
//            String email = jwtUtil.extractUsername(jwt); // assuming you use email in JWT
//            User user = userService.findByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//            // For testing: skip S3 and use placeholder image
//            String imageUrl = "https://via.placeholder.com/150";
//
//            // ML cost prediction (example values)
//            CostPredictionRequest costRequest = new CostPredictionRequest();
//            costRequest.setDistanceKm(150.0); // you can calculate actual distance later
//            costRequest.setWeightKg(weight);
//            costRequest.setLuggageType(category);
//            costRequest.setSpeedPattern(50.0);
//            costRequest.setTrafficFactor(1.0);
//
//            CostPredictionResponse costPrediction = mlService.predictCost(costRequest);
//
//            // Create shipment
//            Shipment shipment = new Shipment();
//            shipment.setUserId(user.getId());
//            shipment.setImageUrl(imageUrl);
//            shipment.setWeight(weight);
//            shipment.setCategory(category);
//            shipment.setSource(source);
//            shipment.setDestination(destination);
//            shipment.setPredictedCost(costPrediction.getPredictedCost());
//
//            Shipment savedShipment = shipmentService.createShipment(shipment);
//
//            return ResponseEntity.ok(savedShipment);
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    
//    
//    // Get shipments of the logged-in user
//    @GetMapping("/my")
//    public ResponseEntity<List<Shipment>> getMyShipments(@RequestHeader("Authorization") String token) {
//        try {
//            String jwt = token.substring(7);
//            String email = jwtUtil.extractUsername(jwt);
//            User user = userService.findByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//            List<Shipment> shipments = shipmentService.getShipmentsByUserId(user.getId());
//            return ResponseEntity.ok(shipments);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    // Get a shipment by tracking ID
//    @GetMapping("/{trackingId}")
//    public ResponseEntity<Shipment> getShipment(@PathVariable String trackingId) {
//        try {
//            Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
//            return ResponseEntity.ok(shipment);
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // Admin: get all shipments
//    @GetMapping("/admin/all")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<List<Shipment>> getAllShipments() {
//        List<Shipment> shipments = shipmentService.getAllShipments();
//        return ResponseEntity.ok(shipments);
//    }
//
//    // Admin: update shipment status
//    @PutMapping("/{trackingId}/status")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<Shipment> updateStatus(
//            @PathVariable String trackingId,
//            @RequestBody Map<String, String> request) {
//        try {
//            String statusStr = request.get("status");
//
//            ShipmentStatus status = ShipmentStatus.valueOf(statusStr.toUpperCase());
//
//            Shipment shipment =
//                    shipmentService.updateShipmentStatus(trackingId, status);
//
//            return ResponseEntity.ok(shipment);
//
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest()
//                    .body(null);
//        }
//    }
//
//}


//v3

package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.dto.CostPredictionRequest;
import com.example.LuggageTracking.model.Payment;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.dto.CostPredictionResponse;
import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.model.User;
import com.example.LuggageTracking.security.JwtUtil;
import com.example.LuggageTracking.service.MLService;
import com.example.LuggageTracking.service.PaymentService;
import com.example.LuggageTracking.service.S3Service;
import com.example.LuggageTracking.service.ShipmentService;
import com.example.LuggageTracking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private MLService mlService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PaymentService paymentService;

    // Create shipment (Step 1: Calculate cost and initiate payment)
    @PostMapping("/create")
    public ResponseEntity<?> createShipment(
            @RequestParam("image") MultipartFile image,
            @RequestParam("source") String source,
            @RequestParam("destination") String destination,
            @RequestParam("weight") Double weight,
            @RequestParam("category") String category,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Placeholder image for now
            String imageUrl = "https://via.placeholder.com/150";

            // ML cost prediction
            CostPredictionRequest costRequest = new CostPredictionRequest();
            costRequest.setDistanceKm(150.0);
            costRequest.setWeightKg(weight);
            costRequest.setLuggageType(category);
            costRequest.setSpeedPattern(50.0);
            costRequest.setTrafficFactor(1.0);

            CostPredictionResponse costPrediction = mlService.predictCost(costRequest);

            // Create shipment
            Shipment shipment = new Shipment();
            shipment.setUserId(user.getId());
            shipment.setImageUrl(imageUrl);
            shipment.setWeight(weight);
            shipment.setCategory(category);
            shipment.setSource(source);
            shipment.setDestination(destination);
            shipment.setPredictedCost(costPrediction.getPredictedCost());
            shipment.setStatus(ShipmentStatus.PENDING);

            Shipment savedShipment = shipmentService.createShipment(shipment);

            // Initiate payment
            Payment payment = paymentService.initiatePayment(
                    savedShipment.getTrackingId(),
                    user.getId(),
                    costPrediction.getPredictedCost()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("shipment", savedShipment);
            response.put("payment", payment);
            response.put("message", "Shipment created. Please complete payment to confirm.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Complete payment and confirm shipment
    @PostMapping("/confirm-payment/{trackingId}")
    public ResponseEntity<?> confirmPayment(
            @PathVariable String trackingId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Get shipment
            Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
            
            // Verify user owns this shipment
            if (!shipment.getUserId().equals(user.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized"));
            }

            // Complete payment
            String paymentMethod = request.get("paymentMethod");
            Payment payment = paymentService.completePayment(trackingId, paymentMethod);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment successful! Your shipment from " + 
                    shipment.getSource() + " to " + shipment.getDestination() + 
                    " has been placed successfully.");
            response.put("trackingId", trackingId);
            response.put("source", shipment.getSource());
            response.put("destination", shipment.getDestination());
            response.put("amount", payment.getAmount());
            response.put("transactionId", payment.getTransactionId());
            response.put("status", "CONFIRMED");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Get my shipments (only confirmed/paid ones)
    @GetMapping("/my")
    public ResponseEntity<List<Shipment>> getMyShipments(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Only return shipments with completed payment
            List<Shipment> shipments = shipmentService.getShipmentsByUserId(user.getId());
            List<Shipment> paidShipments = shipments.stream()
                    .filter(s -> paymentService.isPaymentCompleted(s.getTrackingId()))
                    .toList();

            return ResponseEntity.ok(paidShipments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<Shipment> getShipment(@PathVariable String trackingId) {
        try {
            Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
            return ResponseEntity.ok(shipment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Shipment>> getAllShipments() {
        // Only return paid shipments for admin
        List<Shipment> allShipments = shipmentService.getAllShipments();
        List<Shipment> paidShipments = allShipments.stream()
                .filter(s -> paymentService.isPaymentCompleted(s.getTrackingId()))
                .toList();
        return ResponseEntity.ok(paidShipments);
    }

    @PutMapping("/{trackingId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Shipment> updateStatus(
            @PathVariable String trackingId,
            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            ShipmentStatus status = ShipmentStatus.valueOf(statusStr.toUpperCase());
            Shipment shipment = shipmentService.updateShipmentStatus(trackingId, status);
            return ResponseEntity.ok(shipment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
