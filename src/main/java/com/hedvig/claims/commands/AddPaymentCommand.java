package com.hedvig.claims.commands;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class AddPaymentCommand {

  public String id;
  @TargetAggregateIdentifier
  public String claimID;
  public LocalDateTime date;
  public String userId;

  public Double amount;
  public Double deductible;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public String handlerReference;
}
