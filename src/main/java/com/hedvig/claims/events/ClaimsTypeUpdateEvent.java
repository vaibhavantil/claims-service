package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

@Value
public class ClaimsTypeUpdateEvent {

  @AggregateIdentifier public String claimID;
  public LocalDateTime date;
  public String userId;
  public String type;
}
