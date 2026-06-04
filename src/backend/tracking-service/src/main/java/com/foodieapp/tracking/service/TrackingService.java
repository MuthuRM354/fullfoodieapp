package com.foodieapp.tracking.service;
import com.foodieapp.tracking.model.Location;
import com.foodieapp.tracking.model.TrackingInfo;
import com.foodieapp.tracking.repository.LocationRepository;
import com.foodieapp.tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class TrackingService {
    private final TrackingRepository trackingRepo;
    private final LocationRepository locationRepo;
    public TrackingInfo getTracking(Long orderId) {
        return trackingRepo.findByOrderId(orderId)
            .orElse(TrackingInfo.builder().orderId(orderId).status("NO_TRACKING_INFO").build());
    }
    public TrackingInfo updateTracking(Long orderId, Long partnerId, Double lat, Double lng, String status) {
        TrackingInfo info = trackingRepo.findByOrderId(orderId)
            .orElse(TrackingInfo.builder().orderId(orderId).build());
        info.setDeliveryPartnerId(partnerId);
        info.setLatitude(lat);
        info.setLongitude(lng);
        if (status != null) info.setStatus(status);
        TrackingInfo saved = trackingRepo.save(info);
        // Log to history
        locationRepo.save(Location.builder().orderId(orderId).deliveryPartnerId(partnerId).latitude(lat).longitude(lng).build());
        return saved;
    }
    public List<Location> getHistory(Long orderId) {
        return locationRepo.findByOrderIdOrderByRecordedAtDesc(orderId);
    }
}
