package com.hedvig.claims.events;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ClaimsTypeUpdateEvent {

  public String claimID;
  public LocalDateTime date;
  public String userId;
  public String type;
}
