package hr.hrg.hipster.entityexample.paymentMethod.entity;

import hr.hrg.hipster.entity.api.BooleanOption;
import hr.hrg.hipster.entity.api.View;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public non-sealed interface PayPalPaymentMethod extends PaymentMethod {
    default String type() { return "PAYPAL";}
    String paypalEmail();
    String payerId();
    String payerStatus();
}
