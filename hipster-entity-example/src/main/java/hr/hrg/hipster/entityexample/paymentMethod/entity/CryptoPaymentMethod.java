package hr.hrg.hipster.entityexample.paymentMethod.entity;

import hr.hrg.hipster.entity.api.BooleanOption;
import hr.hrg.hipster.entity.api.View;

@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)
public non-sealed interface CryptoPaymentMethod extends PaymentMethod {
    default String type() { return "CRYPTO";}
    String walletAddress();
    String transactionHash();
    String network();
}
