package com.hedvig.claims.events;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AutomaticPaymentInitiatedEvent {

  private String id;
  private String claimId;
  private String memberId;
  private UUID transactionReference;
  private TransactionStatus transactionStatus;
}
