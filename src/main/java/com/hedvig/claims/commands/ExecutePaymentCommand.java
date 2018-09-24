package com.hedvig.claims.commands;

import javax.money.MonetaryAmount;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class ExecutePaymentCommand {

  private String Id;
  @TargetAggregateIdentifier
  private String claimId;
  private String memberId;
  private MonetaryAmount amount;
  private String note;
  private boolean isExGracia;
  private String handlerReference;
}
