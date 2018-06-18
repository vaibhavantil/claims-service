package com.hedvig.claims.aggregates;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Payment {
	
	@Id
	public String id;
	public Instant date;
	public String userId;
	
	public Double amount;
	public String note;
	public Instant payoutDate;
	public Boolean exGratia;
	
}
