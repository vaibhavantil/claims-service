package com.hedvig.claims.commands;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class FailPayoutCommand {

  private String id;
  @TargetAggregateIdentifier
  private String claimId;
  private String memberId;
  private TransactionStatus transactionStatus;
}
