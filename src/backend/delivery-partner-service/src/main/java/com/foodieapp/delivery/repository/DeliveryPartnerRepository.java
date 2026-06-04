package com.foodieapp.delivery.repository;
import com.foodieapp.delivery.model.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    Optional<DeliveryPartner> findByUserId(Long userId);
    List<DeliveryPartner> findByIsAvailableTrueAndIsActiveTrue();
    List<DeliveryPartner> findByIsActiveTrue();
}
