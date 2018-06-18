package com.hedvig.claims.events;

import java.time.Instant;

import lombok.Value;

@Value
public class ClaimsDataRecievedEvent {

	public String id;
	public String claimID;
	public Instant date;
	public String userId;
	public Double amount;
	
}
