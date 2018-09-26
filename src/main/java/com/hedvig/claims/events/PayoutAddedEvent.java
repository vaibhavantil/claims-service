package com.hedvig.claims.events;

import javax.money.MonetaryAmount;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PayoutAddedEvent {

  private String Id;
  private String claimId;
  private String memberId;
  private MonetaryAmount amount;
  private String note;
  private boolean isExGracia;
  private String handlerReference;
}
