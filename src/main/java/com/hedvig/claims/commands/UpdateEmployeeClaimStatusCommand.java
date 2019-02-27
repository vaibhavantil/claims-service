package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class UpdateEmployeeClaimStatusCommand {
  @TargetAggregateIdentifier
  private String claimId;
  private boolean isCoveringEmployee;

}
