package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import javax.money.MonetaryAmount;

public interface PaymentService {

  PaymentResponse executePayment(String memberId, MonetaryAmount amount);

}
