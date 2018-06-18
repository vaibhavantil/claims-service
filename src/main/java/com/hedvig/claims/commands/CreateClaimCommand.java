package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@Value
public class CreateClaimCommand {

	private static Logger log = LoggerFactory.getLogger(CreateClaimCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    public String userId;
    private Instant registrationDate;
    public String audioURL;

    public CreateClaimCommand(String id, String userId, Instant registrationDate, String audioURL) {
        log.info("InitiateClaimCommand");
        this.id = id;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.audioURL = audioURL;
        log.info(this.toString());
    } 
}
