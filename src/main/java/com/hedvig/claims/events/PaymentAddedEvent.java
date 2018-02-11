package com.hedvig.claims.events;

import lombok.Data;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentAddedEvent {

	private String id;
    private String claimsId;
	public LocalDateTime date;
	public String userId;
	
	public Double amount;
	public String note;
	public LocalDateTime payoutDate;
	public Boolean exGratia;

}
