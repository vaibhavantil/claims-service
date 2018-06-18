package com.hedvig.claims.web.dto;

import java.time.Instant;

public class EventDTO extends HedvigBackofficeDTO{
	
	public String type;
	public String text;
	
	public EventDTO(){}
	
	public EventDTO(String noteId, String claimsId, Instant registrationDate, String userId,
			String text, String typ){
		this.id = noteId;
		this.claimID = claimsId;
		this.date = registrationDate;
		this.userId = userId;
		this.text = text;
		this.type = type;
		
	}
}
