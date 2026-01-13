//package com.example.LuggageTracking.service;
//
//import com.example.LuggageTracking.exception.ResourceNotFoundException;
//import com.example.LuggageTracking.model.Invoice;
//import com.example.LuggageTracking.model.Shipment;
//import com.example.LuggageTracking.repository.InvoiceRepository;
//import com.example.LuggageTracking.util.DistanceCalculator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class BillingService {
//    
//    @Autowired
//    private InvoiceRepository invoiceRepository;
//    
//    @Autowired
//    private ShipmentService shipmentService;
//    
//    @Autowired
//    private DistanceCalculator distanceCalculator;
//    
//    private static final double BASE_FARE = 50.0;
//    private static final double PRICE_PER_KM = 10.0;
//    private static final double PRICE_PER_KG = 20.0;
//    
//    @Transactional
//    public Invoice generateInvoice(String trackingId) {
//        // Check if invoice already exists
//        if (invoiceRepository.existsByTrackingId(trackingId)) {
//            return invoiceRepository.findByTrackingId(trackingId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
//        }
//        
//        Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
//        
//        // Calculate distance (you might want to use actual GPS data)
//        double distance = calculateDistance(shipment.getSource(), shipment.getDestination());
//        
//        // Calculate charges
//        double distanceCharge = distance * PRICE_PER_KM;
//        double weightCharge = shipment.getWeight() * PRICE_PER_KG;
//        
//        // Category multiplier
//        double categoryMultiplier = getCategoryMultiplier(shipment.getCategory());
//        
//        // Calculate total
//        double calculatedTotal = (BASE_FARE + distanceCharge + weightCharge) * categoryMultiplier;
//        
//        // Use AI predicted cost if available, otherwise use calculated
//        double aiPredictedCost = shipment.getPredictedCost() != null ? 
//                                 shipment.getPredictedCost() : calculatedTotal;
//        
//        // Final amount is average of calculated and AI predicted
//        double finalAmount = (calculatedTotal + aiPredictedCost) / 2.0;
//        
//        // Create invoice
//        Invoice invoice = new Invoice();
//        invoice.setTrackingId(trackingId);
//        invoice.setBaseFare(BASE_FARE);
//        invoice.setDistanceKm(distance);
//        invoice.setDistanceCharge(distanceCharge);
//        invoice.setWeightCharge(weightCharge);
//        invoice.setCategoryMultiplier(categoryMultiplier);
//        invoice.setAiPredictedCost(aiPredictedCost);
//        invoice.setFinalAmount(finalAmount);
//        
//        // Update shipment final amount
//        shipment.setFinalAmount(finalAmount);
//        shipmentService.updateShipment(shipment);
//        
//        return invoiceRepository.save(invoice);
//    }
//    
//    public Invoice getInvoice(String trackingId) {
//        return invoiceRepository.findByTrackingId(trackingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for: " + trackingId));
//    }
//    
//    private double calculateDistance(String source, String destination) {
//        // Simplified distance calculation
//        // In production, use actual coordinates or Google Maps API
//        return 150.0; // Default distance in km
//    }
//    
//    private double getCategoryMultiplier(String category) {
//        switch (category.toLowerCase()) {
//            case "fragile":
//                return 1.2;
//            case "express":
//                return 1.5;
//            default:
//                return 1.0;
//        }
//    }
//}



//from 2.3

package com.example.LuggageTracking.service;

import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.model.Invoice;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.repository.InvoiceRepository;
import com.example.LuggageTracking.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private ShipmentService shipmentService;
    
    @Autowired
    private DistanceCalculator distanceCalculator;
    
    private static final double BASE_FARE = 50.0;
    private static final double PRICE_PER_KM = 10.0;
    private static final double PRICE_PER_KG = 20.0;
    
    @Transactional
    public Invoice generateInvoice(String trackingId) {
        // Check if invoice already exists
        if (invoiceRepository.existsByTrackingId(trackingId)) {
            return invoiceRepository.findByTrackingId(trackingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        }
        
        Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
        
        // Calculate distance (simplified - using approximate distance)
        double distance = calculateDistance(shipment.getSource(), shipment.getDestination());
        
        // Calculate charges
        double distanceCharge = distance * PRICE_PER_KM;
        double weightCharge = shipment.getWeight() * PRICE_PER_KG;
        
        // Category multiplier
        double categoryMultiplier = getCategoryMultiplier(shipment.getCategory());
        
        // Calculate total
        double calculatedTotal = (BASE_FARE + distanceCharge + weightCharge) * categoryMultiplier;
        
        // Use AI predicted cost if available, otherwise use calculated
        double aiPredictedCost = shipment.getPredictedCost() != null ? 
                                 shipment.getPredictedCost() : calculatedTotal;
        
        // Final amount is the AI predicted cost (since payment was made based on this)
        double finalAmount = aiPredictedCost;
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setTrackingId(trackingId);
        invoice.setBaseFare(BASE_FARE);
        invoice.setDistanceKm(distance);
        invoice.setDistanceCharge(distanceCharge);
        invoice.setWeightCharge(weightCharge);
        invoice.setCategoryMultiplier(categoryMultiplier);
        invoice.setAiPredictedCost(aiPredictedCost);
        invoice.setFinalAmount(finalAmount);
        
        // Update shipment final amount
        shipment.setFinalAmount(finalAmount);
        shipmentService.updateShipment(shipment);
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice getInvoice(String trackingId) {
        // Try to find existing invoice
        var invoiceOpt = invoiceRepository.findByTrackingId(trackingId);
        
        if (invoiceOpt.isPresent()) {
            return invoiceOpt.get();
        }
        
        // If not found, check if shipment is delivered and generate invoice
        try {
            Shipment shipment = shipmentService.getShipmentByTrackingId(trackingId);
            
            if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
                // Auto-generate invoice for delivered shipment
                return generateInvoice(trackingId);
            }
        } catch (Exception e) {
            // Shipment not found or other error
        }
        
        throw new ResourceNotFoundException("Invoice not found for: " + trackingId);
    }
    
    private double calculateDistance(String source, String destination) {
        // Simplified distance calculation using city pairs
        // In production, use actual coordinates or Google Maps API
        
        // Common Indian routes with approximate distances (in km)
        String route = source + "-" + destination;
        
        switch (route) {
            case "Mumbai-Pune": return 148;
            case "Pune-Mumbai": return 148;
            case "Delhi-Jaipur": return 280;
            case "Jaipur-Delhi": return 280;
            case "Bangalore-Chennai": return 346;
            case "Chennai-Bangalore": return 346;
            case "Mumbai-Delhi": return 1400;
            case "Delhi-Mumbai": return 1400;
            case "Kolkata-Delhi": return 1500;
            case "Delhi-Kolkata": return 1500;
            default: return 150.0; // Default distance
        }
    }
    
    private double getCategoryMultiplier(String category) {
        if (category == null) return 1.0;
        
        switch (category.toLowerCase()) {
            case "fragile":
                return 1.2;
            case "express":
                return 1.5;
            default:
                return 1.0;
        }
    }
}
