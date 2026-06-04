package com.foodieapp.tracking.repository;
import com.foodieapp.tracking.model.TrackingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface TrackingRepository extends JpaRepository<TrackingInfo, Long> {
    Optional<TrackingInfo> findByOrderId(Long orderId);
}
