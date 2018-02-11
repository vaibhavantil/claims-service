package com.hedvig.claims.aggregates;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Note {
	
	@Id
	public String id;
	public LocalDateTime date;
	public String text;
	public String userId;
	public String fileURL;
	
}
