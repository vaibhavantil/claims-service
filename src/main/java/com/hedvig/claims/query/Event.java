package com.hedvig.claims.query;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Event {
	
	@Id
	public String id = UUID.randomUUID().toString();
	public String type;
	public Instant date = Instant.now();
	public String text;
	public String userId;
	
}