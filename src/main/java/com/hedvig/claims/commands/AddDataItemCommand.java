package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class AddDataItemCommand {

	private static Logger log = LoggerFactory.getLogger(AddDataItemCommand.class);
	
	public String id;
	
	@TargetAggregateIdentifier
	public String claimID;
	public LocalDateTime date;
	public String userId;
	
	public DataType type;
	public String name;
	public String title;
	public Boolean recieved;
	public String value;

    public AddDataItemCommand(String id,String claimID, LocalDateTime date, String userId, 
    		DataType type, String name, String title, Boolean recieved, String value) {
        log.info("InitiateClaimCommand");
    	this.id = id;
    	this.claimID = claimID;
    	this.date = date;
    	this.userId = userId;
    	
    	this.type = type;
    	this.name = name;
    	this.title = title;
    	this.recieved = recieved;
    	this.value = value;
        log.info(this.toString());
    } 
}
