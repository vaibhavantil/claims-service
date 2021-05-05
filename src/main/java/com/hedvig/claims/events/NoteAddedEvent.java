package com.hedvig.claims.events;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoteAddedEvent {

  private String id;
  private String claimsId;
  public LocalDateTime date;
  public String text;
  public String userId;
  public String fileURL;
  public String handlerReference; // optional email to iex person
}
