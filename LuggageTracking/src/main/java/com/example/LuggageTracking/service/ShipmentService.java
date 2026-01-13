package com.example.LuggageTracking.service;

import com.example.LuggageTracking.exception.ResourceNotFoundException;
import com.example.LuggageTracking.model.ShipmentStatus;
import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.repository.ShipmentRepository;
import com.example.LuggageTracking.util.TrackingIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private TrackingIdGenerator trackingIdGenerator;
    
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        String trackingId = trackingIdGenerator.generate();
        shipment.setTrackingId(trackingId);
        shipment.setStatus(ShipmentStatus.PENDING);
        return shipmentRepository.save(shipment);
    }
    
    public Shipment getShipmentByTrackingId(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + trackingId));
    }
    
    public List<Shipment> getShipmentsByUserId(Long userId) {
        return shipmentRepository.findByUserId(userId);
    }
    
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }
    
    public List<Shipment> getActiveShipments() {
        return shipmentRepository.findByStatus(ShipmentStatus.IN_TRANSIT);
    }
    
    @Transactional
    public Shipment updateShipmentStatus(String trackingId, ShipmentStatus  status) {
        Shipment shipment = getShipmentByTrackingId(trackingId);
        shipment.setStatus(status);
        
        if (status == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }
        
        return shipmentRepository.save(shipment);
    }
    
    @Transactional
    public Shipment updateShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}
