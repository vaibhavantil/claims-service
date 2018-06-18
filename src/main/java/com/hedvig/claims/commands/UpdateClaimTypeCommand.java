package com.hedvig.claims.commands;

import java.time.Instant;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Value;

@Value
public class UpdateClaimTypeCommand {

    private static Logger log = LoggerFactory.getLogger(UpdateClaimsStateCommand.class);

    @TargetAggregateIdentifier
    private String claimsId;
    private String userId;
    private Instant registrationDate;
    private String type;

    public UpdateClaimTypeCommand(String claimsId, String userId, Instant registrationDate, String type) {
        log.info("UpdateClaimTypeCommand. " + userId + " setting claim with id " + claimsId + " to: " + type);
        this.claimsId = claimsId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.type = type;
        log.info(this.toString());
    }

}
