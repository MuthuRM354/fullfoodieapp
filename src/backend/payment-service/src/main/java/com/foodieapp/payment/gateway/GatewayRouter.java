package com.foodieapp.payment.gateway;

import com.foodieapp.payment.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks which simulated gateway handles a given payment method. CASH never
 * goes through a gateway (it's cash-on-delivery, settled offline). CARD
 * always routes to Razorpay (the most common India card processor). UPI and
 * WALLET are spread across the UPI-style apps for variety, mirroring how a
 * real checkout might let the user pick their app.
 */
@Component
@RequiredArgsConstructor
public class GatewayRouter {

    private final RazorpayGateway razorpayGateway;
    private final PhonePeGateway phonePeGateway;
    private final PaytmGateway paytmGateway;
    private final GooglePayGateway googlePayGateway;
    private final CredGateway credGateway;
    private final BharatPeGateway bharatPeGateway;

    public AbstractMockGateway resolve(PaymentMethod method) {
        return switch (method) {
            case CARD -> razorpayGateway;
            case WALLET -> credGateway;
            case UPI -> pickUpiGateway();
            case CASH -> null; // handled without a gateway call
        };
    }

    private AbstractMockGateway pickUpiGateway() {
        List<AbstractMockGateway> upiGateways = List.of(phonePeGateway, paytmGateway, googlePayGateway, bharatPeGateway);
        return upiGateways.get(ThreadLocalRandom.current().nextInt(upiGateways.size()));
    }
}
