package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

@Value
public class ClaimCreatedEvent {

  @AggregateIdentifier private String id;
  private String userId;
  private LocalDateTime registrationDate;
  private String audioURL;
}
