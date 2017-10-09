package com.hedvig.generic.mustrename.events;

import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;

@Value
public class AssetUpdatedEvent {

	@AggregateIdentifier
    private String id;
    private String name;
    private LocalDate registrationDate;

}
