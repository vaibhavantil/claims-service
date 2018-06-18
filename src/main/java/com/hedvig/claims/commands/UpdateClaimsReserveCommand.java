package com.hedvig.claims.commands;

import java.time.Instant;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Value;

@Value
public class UpdateClaimsReserveCommand {

	private static Logger log = LoggerFactory.getLogger(UpdateClaimsReserveCommand.class);

	@TargetAggregateIdentifier
    public String claimsId;
    private String userId;
    private Instant registrationDate;
    private Double amount;

    public UpdateClaimsReserveCommand(String claimsId, String userId, Instant registrationDate, Double amount) {
        log.info("UpdateClaimsReserveCommand. " + userId + " setting claim with id " + claimsId + " to:" + amount);
        this.claimsId = claimsId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.amount = amount;
        log.info(this.toString());
    }

}
