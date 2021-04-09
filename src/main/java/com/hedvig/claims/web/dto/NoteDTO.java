package com.hedvig.claims.web.dto;

import java.time.LocalDateTime;

public class NoteDTO extends HedvigBackofficeDTO {

  public String text;
  public String fileURL;
  public String handlerReference;

  public NoteDTO() {}

  public NoteDTO(
      String noteId,
      String claimsId,
      LocalDateTime registrationDate,
      String userId,
      String text,
      String fileURL,
      String handlerReference) {
    this.id = noteId;
    this.claimID = claimsId;
    this.date = registrationDate;
    this.userId = userId;
    this.text = text;
    this.fileURL = fileURL;
    this.handlerReference = handlerReference;
  }
}
