package com.hedvig.claims.events;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;
import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

@Value
public class ClaimStatusUpdatedEvent {

  @AggregateIdentifier private String claimsId;
  private String userId;
  private LocalDateTime registrationDate;
  private ClaimStates state;
}
