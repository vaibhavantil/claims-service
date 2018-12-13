package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentAddedEvent {

  private String id;
  private String claimsId;
  public LocalDateTime date;
  public String userId;

  public Double amount;
  public Double deductible;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public String handlerReference;
}
