package com.hedvig.claims.commands;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
@AllArgsConstructor
public class UpdateClaimsDeductibleCommand {

  @TargetAggregateIdentifier public String claimsId;
  private Double amount;

}
