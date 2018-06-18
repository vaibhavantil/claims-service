package com.hedvig.claims.events;

import java.time.Instant;

import lombok.Data;

@Data
public class PaymentAddedEvent {

	private String id;
    private String claimsId;
	public Instant date;
	public String userId;
	
	public Double amount;
	public String note;
	public Instant payoutDate;
	public Boolean exGratia;

}
