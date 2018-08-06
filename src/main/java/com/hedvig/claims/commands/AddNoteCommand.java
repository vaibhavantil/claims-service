package com.hedvig.claims.commands;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class AddNoteCommand {

  private static Logger log = LoggerFactory.getLogger(AddNoteCommand.class);

  public String id;

  @TargetAggregateIdentifier public String claimID;
  public LocalDateTime date;
  public String text;
  public String userId;
  public String fileURL;

  public AddNoteCommand(
      String id, String claimID, LocalDateTime date, String text, String userId, String fileURL) {
    log.info("InitiateClaimCommand");
    this.id = id;
    this.claimID = claimID;
    this.date = date;
    this.text = text;
    this.userId = userId;
    this.fileURL = fileURL;
    log.info(this.toString());
  }
}
