package com.example.LuggageTracking.repository;

import com.example.LuggageTracking.model.Shipment;
import com.example.LuggageTracking.model.ShipmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    List<Shipment> findByUserId(Long userId);
    List<Shipment> findByStatus(ShipmentStatus  status);
    List<Shipment> findByUserIdAndStatus(Long userId, ShipmentStatus  status);
    Optional<Shipment> findByTrackingId(String trackingId);
}
