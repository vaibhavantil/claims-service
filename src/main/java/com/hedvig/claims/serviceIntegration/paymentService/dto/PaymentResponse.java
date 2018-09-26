package com.hedvig.claims.serviceIntegration.paymentService.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class PayoutResponse {

  private UUID paymentReference;
  private PaymentStatus payoutStatus;

  public PayoutResponse(UUID paymentReference,
      PaymentStatus payoutStatus) {
    this.paymentReference = paymentReference;
    this.payoutStatus = payoutStatus;
  }
}
