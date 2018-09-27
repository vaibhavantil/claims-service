package com.hedvig.claims.web.dto;

import com.hedvig.claims.aggregates.Asset;
import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates;
import com.hedvig.claims.aggregates.DataItem;
import com.hedvig.claims.aggregates.Note;
import com.hedvig.claims.aggregates.Payment;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.Event;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimDTO extends HedvigBackofficeDTO {

  public String id;
  public String audioURL;
  public ArrayList<NoteDTO> notes = new ArrayList<NoteDTO>();
  public ArrayList<PaymentDTO> payments = new ArrayList<PaymentDTO>();
  public ArrayList<AssetDTO> assets = new ArrayList<AssetDTO>();
  public List<EventDTO> events = new ArrayList<EventDTO>();
  public ArrayList<DataItemDTO> data = new ArrayList<>();
  public ClaimStates state;
  public Double reserve;
  public String type;

  public ClaimDTO() {
  }

  public ClaimDTO(ClaimEntity c) {
    this.id = c.id;
    this.date = c.registrationDate;
    this.audioURL = c.audioURL;
    this.userId = c.userId;
    this.state = ClaimStates.valueOf(c.state);
    this.claimID = c.id;
    this.reserve = c.reserve;
    this.type = c.type;

    for (Asset a : c.assets) {
      assets.add(new AssetDTO(a.id, c.id, a.date, a.userId));
    }
    for (Payment p : c.payments) {
      payments.add(
          new PaymentDTO(p.id, c.id, p.date, c.userId, p.amount, p.note, p.payoutDate, p.exGratia,
              p.type, p.handlerReference));
    }
    for (Note n : c.notes) {
      notes.add(new NoteDTO(n.id, c.id, n.date, n.userId, n.text, n.fileURL));
    }

    events =
        c.events
            .stream()
            .sorted(Comparator.comparing((Event event) -> event.date).reversed())
            .map(e -> new EventDTO(e.id, c.id, e.date, e.userId, e.text, e.type))
            .collect(Collectors.toList());

    for (DataItem d : c.data) {
      data.add(
          new DataItemDTO(
              d.id, c.id, d.date, d.userId, d.type, d.name, d.title, d.received, d.value));
    }
  }

  public ClaimDTO(
      String id,
      String userId,
      String state,
      Double reserve,
      String type,
      String audioURL,
      LocalDateTime registrationDate) {
    this.id = id;
    this.userId = userId;
    this.state = ClaimStates.valueOf(state);
    this.reserve = reserve;
    this.type = type;
    this.date = registrationDate;
    this.audioURL = audioURL;
  }

  public void addNote(NoteDTO n) {
    this.notes.add(n);
  }

  public String toString() {
    return "\nid:"
        + this.id
        + "\n"
        + "userId:"
        + this.userId
        + "\n"
        + "registrationDate:"
        + this.date
        + "\n"
        + "state:"
        + this.state.toString()
        + "\n"
        + "audioURL:"
        + this.audioURL;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAudioURL() {
    return audioURL;
  }

  public void setAudioURL(String audioURL) {
    this.audioURL = audioURL;
  }

  public LocalDateTime getRegistrationDate() {
    return date;
  }

  public void setRegistrationDate(LocalDateTime registrationDate) {
    this.date = registrationDate;
  }
}
