package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class CreateClaimCommand {

	private static Logger log = LoggerFactory.getLogger(CreateClaimCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    public String userId;
    private LocalDateTime registrationDate;
    public String audioURL;

    public CreateClaimCommand(String userId, String id, LocalDateTime registrationDate, String audioURL) {
        log.info("InitiateClaimCommand");
        this.id = id;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.audioURL = audioURL;
        log.info(this.toString());
    } 
}
