package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ClaimStatusUpdatedEvent {

  private String claimsId;
  private String userId;
  private LocalDateTime registrationDate;
  private ClaimStates state;
}
