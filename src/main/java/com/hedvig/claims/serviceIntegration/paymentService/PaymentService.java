package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutResponse;
import com.hedvig.claims.web.dto.PaymentType;
import java.util.Optional;
import java.util.UUID;
import javax.money.MonetaryAmount;

public interface PaymentService {

  PayoutResponse executePayment(String memberId, MonetaryAmount amount);

}
