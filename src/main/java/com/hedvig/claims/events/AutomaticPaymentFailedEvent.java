package com.hedvig.claims.events;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AutomaticPaymentFailedEvent {

  private String id;
  private String claimId;
  private String memberId;
  private TransactionStatus transactionStatus;
}
