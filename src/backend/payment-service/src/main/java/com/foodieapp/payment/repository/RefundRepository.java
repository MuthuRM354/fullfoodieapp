package com.foodieapp.payment.repository;

import com.foodieapp.payment.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByPaymentId(Long paymentId);
    List<RefundRequest> findByOrderId(Long orderId);
}
