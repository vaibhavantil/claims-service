package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class SetEmployeeClaimCommand {
  @TargetAggregateIdentifier
  private String claimId;
  private boolean isCoveringEmployee;

}
