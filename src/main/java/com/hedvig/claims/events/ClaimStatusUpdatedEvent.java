package com.hedvig.claims.events;

import java.time.Instant;

import org.axonframework.commandhandling.model.AggregateIdentifier;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;

import lombok.Value;

@Value
public class ClaimStatusUpdatedEvent {

	@AggregateIdentifier
    private String claimsId;
	private String userId;
    private Instant registrationDate;
    private ClaimStates state;

}
