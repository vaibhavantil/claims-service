package com.hedvig.claims.serviceIntegration.paymentService.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class PaymentResponse {

  private UUID transactionReference;
  private TransactionStatus transactionStatus;

  public PaymentResponse(UUID transactionReference,
      TransactionStatus transactionStatus) {
    this.transactionReference = transactionReference;
    this.transactionStatus = transactionStatus;
  }
}
