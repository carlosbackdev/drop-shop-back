package com.motogear.dropshopback.shop.order.web;

import com.motogear.dropshopback.common.messages.service.OrderStatusMessageService;
import com.motogear.dropshopback.shop.order.domain.Tracking;
import com.motogear.dropshopback.shop.order.dto.TrackRequest;
import com.motogear.dropshopback.shop.order.service.TrackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackService trackService;
    private final OrderStatusMessageService orderStatusMessageService;

    @PostMapping("/admin/scrape")
    public ResponseEntity<Tracking> scrapeTrackingAdmin(@RequestBody TrackRequest request) {
        Tracking tracking= trackService.getTrackingByNumber(request.getTrackingNumber());
        tracking = trackService.scrapeTrack(request.getTrackingNumber(),tracking);
        if (tracking.getId()==null) { return ResponseEntity.badRequest().build(); }
        trackService.saveTracking(tracking, request.getOrderId());
        orderStatusMessageService.handleOrderDelivering(request.getOrderId());
        return ResponseEntity.ok(tracking);
    }

    //implementar solo dos veces al dia para no saturar el servicio de terceros
    @PostMapping("/track-udpate/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Tracking> scrapeTracking(@PathVariable Long orderId) {
        Tracking tracking= trackService.getTrackingByOrderId(orderId);
        tracking = trackService.scrapeTrack(tracking.getTrackingNumber(),tracking);
        trackService.saveTracking(tracking,orderId);
        return ResponseEntity.ok(tracking);
    }

    @GetMapping("/track-order/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Tracking> getTracking(@PathVariable Long orderId) {
        Tracking tracking= trackService.getTrackingByOrderId(orderId);
        if (tracking==null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tracking);
    }

    @GetMapping("/track-number/{trackId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Tracking> getTrackingByNumber(@PathVariable String trackId) {
        System.out.println("Fetching tracking info for: " + trackId);
        Tracking tracking= trackService.getTrackingByNumber(trackId);
        return ResponseEntity.ok(tracking);
    }

}

