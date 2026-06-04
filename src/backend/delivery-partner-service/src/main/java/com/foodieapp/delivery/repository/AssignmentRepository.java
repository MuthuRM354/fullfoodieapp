package com.foodieapp.delivery.repository;
import com.foodieapp.delivery.model.Assignment;
import com.foodieapp.delivery.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByDeliveryPartnerId(Long deliveryPartnerId);
    Optional<Assignment> findByOrderId(Long orderId);
    List<Assignment> findByDeliveryPartnerIdAndStatus(Long deliveryPartnerId, AssignmentStatus status);
}
