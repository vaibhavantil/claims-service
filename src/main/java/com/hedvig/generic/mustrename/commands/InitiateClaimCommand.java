package com.hedvig.generic.mustrename.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.mustrename.query.ClaimsEventListener;

import java.time.LocalDate;

@Value
public class InitiateClaimCommand {

	private static Logger log = LoggerFactory.getLogger(InitiateClaimCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    public String userId;
    private LocalDate registrationDate;

    public InitiateClaimCommand(String userId, String id, LocalDate registrationDate) {
        log.info("CreateAssetCommand");
        this.id = id;
        this.userId = userId;
        this.registrationDate = registrationDate;
        log.info(this.toString());
    } 
}
