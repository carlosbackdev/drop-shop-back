package com.motogear.dropshopback.shop.order.repository;

import com.motogear.dropshopback.shop.order.domain.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    Optional<Tracking> findByTrackingNumber(String trackingNumber);

    Optional<Tracking> findByOrderId(Long orderId);
}

