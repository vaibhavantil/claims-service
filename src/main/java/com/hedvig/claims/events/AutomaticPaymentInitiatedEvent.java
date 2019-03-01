package com.hedvig.claims.events;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class AutomaticPaymentInitiatedEvent {

  private String id;
  private String claimId;
  private String memberId;
  private UUID transactionReference;
  private TransactionStatus transactionStatus;
}
