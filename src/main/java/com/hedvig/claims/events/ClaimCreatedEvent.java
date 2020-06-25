package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Value
@Revision("2.0")
public class ClaimCreatedEvent {
  public String id;
  public String userId;
  public String audioURL;
  public UUID contractId;
}
