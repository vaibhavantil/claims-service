package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ClaimStatusUpdatedEvent {
  public String claimsId;
  public String userId;
  public LocalDateTime registrationDate;
  public ClaimStates state;
}
