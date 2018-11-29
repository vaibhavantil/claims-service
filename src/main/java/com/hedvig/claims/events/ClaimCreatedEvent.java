package com.hedvig.claims.events;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.serialization.Revision;

@Value
@Revision("1.0")
public class ClaimCreatedEvent {

  @AggregateIdentifier private String id;
  private String userId;
  private Instant registrationDate;
  private String audioURL;
}
