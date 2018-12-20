package com.hedvig.claims.commands;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class CreateClaimCommand {

  private static Logger log = LoggerFactory.getLogger(CreateClaimCommand.class);

  @TargetAggregateIdentifier public String id;
  public String userId;
  public String audioURL;

  public CreateClaimCommand(
      String id, String userId, String audioURL) {
    log.info("InitiateClaimCommand");
    this.id = id;
    this.userId = userId;
    this.audioURL = audioURL;
    log.info(this.toString());
  }
}
