package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Revision("1.1")
public class ClaimCreatedEvent {
  private String id;
  private String userId;
  private String audioURL;
}
