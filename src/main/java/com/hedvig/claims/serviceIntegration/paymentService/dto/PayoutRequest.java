package com.hedvig.claims.serviceIntegration.paymentService.dto;

import javax.money.MonetaryAmount;
import lombok.Value;

@Value
public class PayoutRequest {

  MonetaryAmount amount;
  boolean sanctionBypassed;
}
