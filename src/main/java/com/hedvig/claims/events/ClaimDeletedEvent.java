package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;

@Value
public class ClaimDeletedEvent {

	@AggregateIdentifier
    private String id;

}
