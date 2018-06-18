package com.hedvig.claims.web.dto;

import java.time.Instant;

public class PaymentDTO extends HedvigBackofficeDTO{

	public Double amount;
	public String note;
	public Instant payoutDate;
	public Boolean exGratia;
	
	public PaymentDTO(){}
	
	public PaymentDTO(String paymentId, String claimsId, Instant registrationDate, String userId,
			Double amount, String note, Instant payoutDate, Boolean exGratia){
		this.id = paymentId;
		this.claimID = claimsId;
		this.date = registrationDate;
		this.userId = userId;
		this.amount = amount;
		this.note = note;
		this.payoutDate = payoutDate;
		this.exGratia = exGratia;
	}
	
}
