package com.hedvig.claims.commands;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class UpdateClaimsReserveCommand {

  private static Logger log = LoggerFactory.getLogger(UpdateClaimsReserveCommand.class);

  @TargetAggregateIdentifier public String claimsId;
  private String userId;
  private LocalDateTime registrationDate;
  private Double amount;

  public UpdateClaimsReserveCommand(
      String claimsId, String userId, LocalDateTime registrationDate, Double amount) {
    log.info(
        "UpdateClaimsReserveCommand. "
            + userId
            + " setting claim with id "
            + claimsId
            + " to:"
            + amount);
    this.claimsId = claimsId;
    this.userId = userId;
    this.registrationDate = registrationDate;
    this.amount = amount;
    log.info(this.toString());
  }
}
