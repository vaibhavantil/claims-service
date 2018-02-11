package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class ClaimStatusUpdatedEvent {

	@AggregateIdentifier
    private String claimsId;
	private String userId;
    private LocalDateTime registrationDate;
    private ClaimStates state;

}
