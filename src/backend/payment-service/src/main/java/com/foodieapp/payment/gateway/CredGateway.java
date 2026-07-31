package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class CredGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "CredGateway";
    }
}
