package com.hedvig.claims.events;

import lombok.Value;

@Value
public class EmployeeClaimStatusUpdatedEvent {
  private String claimId;
  private boolean isCoveringEmployee;
}
