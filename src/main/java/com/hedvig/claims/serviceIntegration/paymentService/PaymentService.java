package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutStatus;
import javax.money.MonetaryAmount;

public interface PaymentService {

  PayoutStatus executePayment(String memberId, MonetaryAmount amount);

}
