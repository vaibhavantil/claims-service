package com.hedvig.claims.fraud;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FraudParameters {
	
	@Id
	public String id;
	public Instant date;
	public Instant signupDate;
	public Integer numberOfClaims;
	
}
