package com.hedvig.claims.aggregates;

import com.hedvig.claims.web.dto.PaymentType;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Payment {

  @Id
  public String id;
  public LocalDateTime date;
  public String userId;

  public Double amount;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public PaymentType type;
  public String memberExperienceMail;
}
