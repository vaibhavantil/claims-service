package com.hedvig.claims.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.model.AggregateIdentifier;

@Data
@AllArgsConstructor
public class ClaimsDeductibleUpdateEvent {

  @AggregateIdentifier private String claimID;
  private Double amount;
}
