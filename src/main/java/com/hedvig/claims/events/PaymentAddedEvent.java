package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Revision("1.0")
public class PaymentAddedEvent {

  String id;
  String claimsId;
  LocalDateTime date;
  String userId;

  Double amount;
  Double deductible;
  String note;
  LocalDateTime payoutDate;
  Boolean exGratia;
  String handlerReference;
}
