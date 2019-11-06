package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimSource;
import lombok.Value;

import java.time.Instant;

@Value
public class BackofficeClaimCreatedEvent {
  private String id;
  private String memberId;
  private Instant registrationDate;
  private ClaimSource claimSource;
}
