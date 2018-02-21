package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Value
public class UpdateClaimTypeCommand {

    private static Logger log = LoggerFactory.getLogger(UpdateClaimsStateCommand.class);

    @TargetAggregateIdentifier
    private String claimsId;
    private String userId;
    private LocalDateTime registrationDate;
    private String type;

    public UpdateClaimTypeCommand(String claimsId, String userId, LocalDateTime registrationDate, String type) {
        log.info("UpdateClaimTypeCommand. " + userId + " setting claim with id " + claimsId + " to: " + type);
        this.claimsId = claimsId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.type = type;
        log.info(this.toString());
    }

}
