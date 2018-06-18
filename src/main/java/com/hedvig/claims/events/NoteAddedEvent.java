package com.hedvig.claims.events;

import java.time.Instant;

import lombok.Data;

@Data
public class NoteAddedEvent {

	private String id;
    private String claimsId;
	public Instant date;
	public String text;
	public String userId;
	public String fileURL;

}
