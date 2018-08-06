package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class ClaimsDataRecievedEvent {

  public String id;
  public String claimID;
  public LocalDateTime date;
  public String userId;
  public Double amount;
}
