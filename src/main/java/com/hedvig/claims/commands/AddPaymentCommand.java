package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class AddPaymentCommand {

	private static Logger log = LoggerFactory.getLogger(AddPaymentCommand.class);

	public String id;
	@TargetAggregateIdentifier
	public String claimID;
	public LocalDateTime date;
	public String userId;
	
	public Double amount;
	public String note;
	public LocalDateTime payoutDate;
	public Boolean exGratia;

}
