package com.hedvig.claims.events;

import lombok.Value;

@Value
public class EmployeeClaimSetEvent {
  private String claimId;
  private boolean isCoveringEmployee;
}
