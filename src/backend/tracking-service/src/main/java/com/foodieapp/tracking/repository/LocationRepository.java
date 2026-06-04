package com.foodieapp.tracking.repository;
import com.foodieapp.tracking.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByOrderIdOrderByRecordedAtDesc(Long orderId);
}
