package com.hedvig.claims.aggregates;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Note {
	
	@Id
	public String id;
	public Instant date;
	public String text;
	public String memberId;
	public String fileURL;
	
}
