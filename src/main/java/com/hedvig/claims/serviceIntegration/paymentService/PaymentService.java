package com.hedvig.claims.serviceIntegration.paymentService;

import java.util.Optional;
import java.util.UUID;
import javax.money.MonetaryAmount;

public interface PaymentService {

  Optional<UUID> executePayment(String memberId, MonetaryAmount amount);

}
