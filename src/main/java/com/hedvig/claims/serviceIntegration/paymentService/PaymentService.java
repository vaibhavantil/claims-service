package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;

public interface PaymentService {

  PaymentResponse executePayment(String memberId, PayoutRequest request);

}
