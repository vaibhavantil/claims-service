package com.hedvig.claims.events;

import com.hedvig.claims.web.dto.PaymentType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentAddedEvent {

  private String id;
  private String claimsId;
  public LocalDateTime date;
  public String userId;

  public Double amount;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public PaymentType type;
  public String claimHandlerMail;
}
