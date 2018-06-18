package com.hedvig.claims.events;

import java.time.Instant;

import org.axonframework.commandhandling.model.AggregateIdentifier;

import lombok.Value;

@Value
public class ClaimCreatedEvent {

	@AggregateIdentifier
    private String id;
	private String userId;
    private Instant registrationDate;
    private String audioURL;

}
