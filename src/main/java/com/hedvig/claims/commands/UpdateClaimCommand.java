package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;

import java.time.LocalDate;

@Value
public class UpdateClaimCommand {

	private static Logger log = LoggerFactory.getLogger(UpdateClaimCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    private String name;
    private LocalDate registrationDate;

    public UpdateClaimCommand(String id, String name, LocalDate registrationDate) {
        log.info("UpdateAssetCommand");
        this.id = id;
        this.name = name;
        this.registrationDate = registrationDate;
        log.info(this.toString());
    } 
}
