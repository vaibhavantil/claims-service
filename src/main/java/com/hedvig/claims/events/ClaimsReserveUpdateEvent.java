package com.hedvig.claims.events;

import java.time.Instant;

import org.axonframework.commandhandling.model.AggregateIdentifier;

import lombok.Value;

@Value
public class ClaimsReserveUpdateEvent {

	@AggregateIdentifier
	public String claimID;
	public Instant date;
	public String userId;
	public Double amount;
	
}
