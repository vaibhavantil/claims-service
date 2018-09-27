package com.hedvig.claims.commands;

import com.hedvig.claims.web.dto.PaymentType;
import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class AddPaymentCommand {

  private static Logger log = LoggerFactory.getLogger(AddPaymentCommand.class);

  public String id;
  @TargetAggregateIdentifier
  public String claimID;
  public LocalDateTime date;
  public String userId;

  public Double amount;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public String handlerReference;
}
