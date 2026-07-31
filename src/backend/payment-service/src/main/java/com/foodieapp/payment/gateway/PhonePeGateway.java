package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class PhonePeGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "PhonePeGateway";
    }
}
