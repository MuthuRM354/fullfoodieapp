package com.foodieapp.api.metrics;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
public class GatewayMetrics {
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);

    public void recordRequest(boolean success) {
        totalRequests.incrementAndGet();
        if (success) successfulRequests.incrementAndGet();
        else failedRequests.incrementAndGet();
    }
}
