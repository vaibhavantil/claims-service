package com.hedvig.claims.events;

import javax.money.MonetaryAmount;
import lombok.Value;

@Value
public class PaymentExecutedEvent {

  private String Id;
  private String claimId;
  private String memberId;
  private MonetaryAmount amount;
  private String note;
  private boolean isExGracia;
  private String handlerReference;

  public PaymentExecutedEvent(String id, String claimId, String memberId,
      MonetaryAmount amount, String note, boolean isExGracia, String handlerReference) {
    Id = id;
    this.claimId = claimId;
    this.memberId = memberId;
    this.amount = amount;
    this.note = note;
    this.isExGracia = isExGracia;
    this.handlerReference = handlerReference;
  }
}
