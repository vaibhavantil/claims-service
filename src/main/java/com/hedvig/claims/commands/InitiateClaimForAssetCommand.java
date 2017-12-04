package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.query.ClaimsEventListener;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class InitiateClaimForAssetCommand {

	private static Logger log = LoggerFactory.getLogger(InitiateClaimForAssetCommand.class);
	
    @TargetAggregateIdentifier
    public String id;
    public String userId;
    public UUID assetId;
    private LocalDate registrationDate;

    public InitiateClaimForAssetCommand(String userId, String id, UUID assetId, LocalDate registrationDate) {
        log.info("InitiateClaimForAssetCommand");
        this.id = id;
        this.userId = userId;
        this.assetId = assetId;
        this.registrationDate = registrationDate;
        log.info(this.toString());
    } 
}
