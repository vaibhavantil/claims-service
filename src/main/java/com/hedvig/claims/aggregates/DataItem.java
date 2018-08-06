package com.hedvig.claims.aggregates;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DataItem {

  @Id public String id;
  public LocalDateTime date;
  public String userId;

  public DataType type;
  public String name;
  public String title;
  public Boolean received;
  public String value;
}
