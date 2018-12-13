package com.hedvig.claims.events;

import javax.money.MonetaryAmount;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AutomaticPaymentAddedEvent {

  String Id;
  String claimId;
  String memberId;
  MonetaryAmount amount;
  MonetaryAmount deductible;
  String note;
  boolean isExGracia;
  String handlerReference;
  boolean sanctionCheckSkipped;
}
