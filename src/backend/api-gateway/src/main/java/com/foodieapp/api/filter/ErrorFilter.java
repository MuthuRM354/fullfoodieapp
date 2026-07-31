package com.foodieapp.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(4)
@Slf4j
public class ErrorFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (Exception ex) {
            log.error("Unhandled gateway error: {}", ex.getMessage(), ex);
            HttpServletResponse response = (HttpServletResponse) res;
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Gateway internal error\"}");
            }
        }
    }
}
