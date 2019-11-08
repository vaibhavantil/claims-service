package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimSource;
import lombok.Value;

import java.time.Instant;

@Value
public class BackofficeClaimCreatedEvent {
  public String id;
  public String memberId;
  public Instant registrationDate;
  public ClaimSource claimSource;
}
