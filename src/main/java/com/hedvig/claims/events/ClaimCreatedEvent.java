package com.hedvig.claims.events;

import lombok.Value;
import org.apache.tomcat.jni.Local;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;

@Value
public class ClaimCreatedEvent {

	@AggregateIdentifier
    private String id;
	private String userId;
    private LocalDate registrationDate;

}
