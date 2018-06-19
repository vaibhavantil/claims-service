package com.hedvig.claims.fraud;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class FraudModel {
	
	@Id
	public String id;
	public Instant date;
	public Double risk;
	
	@Transient
	private FraudParameters params; 
	
	@Transient
	private FraudResult result; 
	
	// TODO: Set parameters to correct values
	/*
	 * The mean number of hours since signup where a risk is classified as less than 50% fraud risk
	 * */
	public static double meanSignupDaysRisk = 7;
	public static double meanNumberOfPreviousClaims = 6;
	
	public FraudModel(FraudParameters params){ 
		this.params = params;
	}
	
	public void calculateFraudRisk(){
		if(this.params==null){
			throw new InvalidFraudParametersException();
		}
		
		this.result = new FraudResult();
		
		this.date = Instant.now();
		Long daysSinceSignup = Math.abs(Duration.between(params.date, params.signupDate).toDays());
		
		System.out.println(daysSinceSignup + ":" + params.numberOfClaims);
		
		Double p1 = Math.exp(- ((double)daysSinceSignup/meanSignupDaysRisk));
		Double p2 = Math.exp(- ((double)params.numberOfClaims/meanNumberOfPreviousClaims));
		
		System.out.println(p1 + " " + p2);
		
		this.result.params.put("Days since signup", p1);
		this.result.params.put("Number of previous claims", p2);
	}
	
	public void setParameters(FraudParameters params){
		this.params = params;
	}
	
	public FraudResult getFraudRisk(){
		if(this.risk == null)calculateFraudRisk();
		
		return this.result;
	}
	
	public static void main(String args[]){
		FraudParameters p = new FraudParameters();
		p.date = Instant.now();
		try {
			p.signupDate = new SimpleDateFormat("yyyy-MM-dd").parse("2018-06-17").toInstant();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.numberOfClaims = 5;
		
		FraudModel model = new FraudModel(p);
		System.out.println("Model risk:" + model.risk);
	}
}
