package com.hedvig.generic.mustrename.events;

import lombok.Value;
import org.apache.tomcat.jni.Local;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;

@Value
public class AssetCreatedEvent {

	@AggregateIdentifier
    private String id;
	private String userId;
    private String name;
    private LocalDate registrationDate;

}
