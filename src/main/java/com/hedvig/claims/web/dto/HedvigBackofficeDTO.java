package com.hedvig.claims.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;

/*
 * Superclass containing meta information required for each data edit
 * */
public class HedvigBackofficeDTO {

  public String id;
  public String claimID;
  public LocalDateTime date;
  public Instant dateInstant;
  public String userId;
}
