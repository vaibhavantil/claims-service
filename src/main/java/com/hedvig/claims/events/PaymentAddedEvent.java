package com.hedvig.claims.events;

import lombok.Value;
import org.axonframework.serialization.Revision;

import java.time.LocalDateTime;

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
  Boolean exGratia;
  String handlerReference;
}
