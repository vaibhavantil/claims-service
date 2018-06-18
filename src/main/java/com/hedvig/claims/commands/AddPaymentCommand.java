package com.hedvig.claims.commands;

import java.time.Instant;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Value;

@Value
public class AddPaymentCommand {

	private static Logger log = LoggerFactory.getLogger(AddPaymentCommand.class);

	public String id;
	@TargetAggregateIdentifier
	public String claimID;
	public Instant date;
	public String userId;
	
	public Double amount;
	public String note;
	public Instant payoutDate;
	public Boolean exGratia;

}
