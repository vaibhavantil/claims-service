package com.hedvig.claims.events;

import lombok.Value;
import org.apache.tomcat.jni.Local;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class AssetClaimCreatedEvent {

	@AggregateIdentifier
    private String id;
	private String userId;
    private UUID assetId;
    private LocalDate registrationDate;

}
