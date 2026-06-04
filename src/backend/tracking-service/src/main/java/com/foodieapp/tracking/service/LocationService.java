package com.foodieapp.tracking.service;
import com.foodieapp.tracking.model.Location;
import com.foodieapp.tracking.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class LocationService {
    private final LocationRepository repo;
    public List<Location> getHistory(Long orderId) { return repo.findByOrderIdOrderByRecordedAtDesc(orderId); }
    public Location save(Location location) { return repo.save(location); }
}
