package com.hedvig.claims.commands;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import java.util.UUID;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class AddInitiatedAutomaticPaymentCommand {

  private String id;
  @TargetAggregateIdentifier
  private String claimId;
  private String memberId;
  private UUID transactionReference;
  private TransactionStatus transactionStatus;
}
