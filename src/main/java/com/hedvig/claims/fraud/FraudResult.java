package com.hedvig.claims.fraud;

import java.time.Instant;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FraudResult {
	
	@Id
	public String id;
	public Instant date;
	public Double fraudRisk;
	public HashMap<String, Double> params;
}
