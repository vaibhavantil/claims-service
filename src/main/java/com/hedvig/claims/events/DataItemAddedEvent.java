package com.hedvig.claims.events;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DataItemAddedEvent {

  private String id;
  private String claimsId;
  public LocalDateTime date;
  public String userId;

  public DataType type;
  public String name;
  public String title;
  public Boolean received;
  public String value;
}
