package com.hedvig.claims.commands;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Value
public class CreateClaimCommand {

  private static Logger log = LoggerFactory.getLogger(CreateClaimCommand.class);

  @TargetAggregateIdentifier public String id;
  public String userId;
  public String audioURL;
  public UUID contactId;

  public CreateClaimCommand(
      String id,
      String userId,
      String audioURL,
      UUID contactId
  ) {
    log.info("InitiateClaimCommand");
    this.id = id;
    this.userId = userId;
    this.audioURL = audioURL;
    this.contactId = contactId;
    log.info(this.toString());
  }
}
