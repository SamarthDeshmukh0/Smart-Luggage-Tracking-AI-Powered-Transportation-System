package com.example.LuggageTracking.controller;

import com.example.LuggageTracking.model.Invoice;
import com.example.LuggageTracking.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {
    
    @Autowired
    private BillingService billingService;
    
    @PostMapping("/generate/{trackingId}")
    public ResponseEntity<?> generateInvoice(@PathVariable String trackingId) {
        try {
            Invoice invoice = billingService.generateInvoice(trackingId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/invoice/{trackingId}")
    public ResponseEntity<?> getInvoice(@PathVariable String trackingId) {
        try {
            Invoice invoice = billingService.getInvoice(trackingId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
