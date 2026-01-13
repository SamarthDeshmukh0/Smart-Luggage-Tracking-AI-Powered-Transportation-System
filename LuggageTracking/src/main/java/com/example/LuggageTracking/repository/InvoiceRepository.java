package com.example.LuggageTracking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.LuggageTracking.model.Invoice;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByTrackingId(String trackingId);
    Boolean existsByTrackingId(String trackingId);

    @Query("SELECT SUM(i.finalAmount) FROM Invoice i")
    Double getTotalRevenue();

}