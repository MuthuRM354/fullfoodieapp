package com.foodieapp.payment.gateway;

import org.springframework.stereotype.Component;

@Component
public class BharatPeGateway extends AbstractMockGateway {
    @Override
    protected String gatewayName() {
        return "BharatPeGateway";
    }
}
