package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class RazorpayGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "RazorpayGateway";
    }
}
