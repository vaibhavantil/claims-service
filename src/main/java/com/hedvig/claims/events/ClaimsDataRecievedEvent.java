package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class ClaimsDataRecievedEvent {

	public String id;
	public String claimID;
	public LocalDateTime date;
	public String userId;
	public Double amount;
	
}
