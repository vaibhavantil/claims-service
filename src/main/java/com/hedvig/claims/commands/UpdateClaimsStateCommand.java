package com.hedvig.claims.commands;

import java.time.Instant;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;

import lombok.Value;

@Value
public class UpdateClaimsStateCommand {

	private static Logger log = LoggerFactory.getLogger(UpdateClaimsStateCommand.class);

	@TargetAggregateIdentifier
    public String claimsId;
    private String userId;
    private Instant registrationDate;
    private ClaimStates state;

    public UpdateClaimsStateCommand(String claimsId, String userId, Instant registrationDate, ClaimStates state) {
        log.info("UpdateClaimsStateCommand. " + userId + " setting claim with id " + claimsId + " to:" + state);
        this.claimsId = claimsId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.state = state;
        log.info(this.toString());
    }

}
