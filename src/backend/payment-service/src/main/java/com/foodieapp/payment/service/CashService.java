package com.foodieapp.payment.service;

import com.foodieapp.payment.model.CashPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class CashService {

    public Map<String, Object> confirmCashPayment(Long orderId, BigDecimal amount, String collectedBy) {
        log.info("Cash payment confirmed: order={} amount={} collectedBy={}", orderId, amount, collectedBy);
        return Map.of(
            "success", true,
            "orderId", orderId,
            "amount", amount,
            "collectedBy", collectedBy != null ? collectedBy : "DELIVERY_PARTNER",
            "status", "COLLECTED"
        );
    }
}
