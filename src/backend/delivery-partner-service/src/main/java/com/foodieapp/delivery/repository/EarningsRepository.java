package com.foodieapp.delivery.repository;
import com.foodieapp.delivery.model.Earnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface EarningsRepository extends JpaRepository<Earnings, Long> {
    List<Earnings> findByDeliveryPartnerId(Long deliveryPartnerId);
    @Query("SELECT SUM(e.amount) FROM Earnings e WHERE e.deliveryPartnerId = :partnerId")
    BigDecimal sumByDeliveryPartnerId(@Param("partnerId") Long partnerId);
}
