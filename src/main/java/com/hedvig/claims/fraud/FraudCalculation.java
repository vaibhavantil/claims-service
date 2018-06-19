package com.hedvig.claims.fraud;

import java.time.Instant;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FraudCalculation {
	
	@Id
	public String id;
	public String memberId;
	public Instant date;
	public FraudModel model;
	public FraudParameters params;
	public FraudResult result;
	
	public FraudResult calculateFraudRisk(FraudModel model, FraudParameters params){
		model.setParameters(params);
		this.result = model.getFraudRisk();
		
		// Take the largest individual risk parameter as the overall risk
		for (Map.Entry<String, Double> entry : this.result.params.entrySet()) {
		    Double value = entry.getValue();
		    if(result.fraudRisk != null){
		    	result.fraudRisk = Math.max(result.fraudRisk, value);
		    }else{
		    	result.fraudRisk = value;
		    }
		}
		
		return null;
	}
	
}