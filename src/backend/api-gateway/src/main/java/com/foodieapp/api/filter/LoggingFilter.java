package com.foodieapp.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        long start = System.currentTimeMillis();
        chain.doFilter(req, res);
        long duration = System.currentTimeMillis() - start;
        log.info("[Gateway] {} {} -> {} ({}ms)",
                request.getMethod(), request.getRequestURI(),
                response.getStatus(), duration);
    }
}
