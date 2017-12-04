package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;

import java.time.LocalDate;

@Value
public class DeleteClaimCommand {

	private static Logger log = LoggerFactory.getLogger(DeleteClaimCommand.class);
	
    @TargetAggregateIdentifier
    public String id;

    public DeleteClaimCommand(String id) {
        log.info("DeleteAssetCommand");
        this.id = id;
        log.info(this.toString());
    } 
}
