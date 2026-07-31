package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class PaytmGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "PaytmGateway";
    }
}
