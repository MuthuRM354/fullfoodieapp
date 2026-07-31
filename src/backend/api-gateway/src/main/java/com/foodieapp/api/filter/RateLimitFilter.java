package com.foodieapp.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(3)
@Slf4j
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private final ConcurrentHashMap<String, RequestCount> counts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String clientIp = getClientIp(request);
        RequestCount rc = counts.computeIfAbsent(clientIp, k -> new RequestCount());

        long now = System.currentTimeMillis();
        if (now - rc.windowStart > 60_000) {
            rc.count.set(0);
            rc.windowStart = now;
        }

        if (rc.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please slow down.\"}");
            return;
        }

        chain.doFilter(req, res);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    private static class RequestCount {
        AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
    }
}
