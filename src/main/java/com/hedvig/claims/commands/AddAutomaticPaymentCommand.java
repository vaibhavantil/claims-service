package com.hedvig.claims.commands;

import javax.money.MonetaryAmount;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class AddAutomaticPaymentCommand {

  @TargetAggregateIdentifier
  String claimId;
  String memberId;
  MonetaryAmount amount;
  MonetaryAmount deductible;
  String note;
  boolean isExGracia;
  String handlerReference;
  boolean sanctionCheckSkipped;
}
