package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimSource;
import lombok.Value;
import org.axonframework.serialization.Revision;

import java.time.Instant;
import java.util.UUID;

@Value
@Revision("1.0")
public class BackofficeClaimCreatedEvent {
  public String id;
  public String memberId;
  public Instant registrationDate;
  public ClaimSource claimSource;
  public UUID contractId;
}
