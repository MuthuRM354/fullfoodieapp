package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class GooglePayGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "GooglePayGateway";
    }
}
