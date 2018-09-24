package com.hedvig.claims.aggregates;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.ExecutePaymentCommand;
import com.hedvig.claims.commands.UpdateClaimTypeCommand;
import com.hedvig.claims.commands.UpdateClaimsReserveCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import com.hedvig.claims.events.ClaimsTypeUpdateEvent;
import com.hedvig.claims.events.DataItemAddedEvent;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.events.PaymentAddedEvent;
import com.hedvig.claims.events.PaymentExecutedEvent;
import com.hedvig.claims.web.dto.PaymentType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class ClaimsAggregate {

  private static Logger log = LoggerFactory.getLogger(ClaimsAggregate.class);
  private static final String SWEDEN_TIMEZONE = "Europe/Stockholm";

  public enum ClaimStates {
    OPEN,
    CLOSED,
    REOPENED
  }

  @AggregateIdentifier
  public String id;

  public String userId;
  public String audioURL;
  public LocalDateTime registrationDate;
  public ClaimStates state;
  public Double reserve;
  public String type;

  public ArrayList<DataItem> data;
  public ArrayList<Payment> payments;
  public ArrayList<Note> notes;
  public ArrayList<String> assets;

  public ClaimsAggregate() {
    log.info("Instansiating ClaimsAggregate");
  }

  @CommandHandler
  public ClaimsAggregate(CreateClaimCommand command) {
    log.info("create claim");
    apply(
        new ClaimCreatedEvent(
            command.getId(),
            command.getUserId(),
            command.getRegistrationDate(),
            command.getAudioURL()));
  }

  @CommandHandler
  public void update(UpdateClaimsStateCommand command) {
    log.info("update claim state");
    apply(
        new ClaimStatusUpdatedEvent(
            command.getClaimsId(),
            command.getUserId(),
            command.getRegistrationDate(),
            command.getState()));
  }

  @CommandHandler
  public void updateReserve(UpdateClaimsReserveCommand command) {
    log.info("update claim reserve");
    apply(
        new ClaimsReserveUpdateEvent(
            command.getClaimsId(),
            command.getRegistrationDate(),
            command.getUserId(),
            command.getAmount()));
  }

  @CommandHandler
  public void updateType(UpdateClaimTypeCommand command) {
    log.info("update claim type");
    apply(
        new ClaimsTypeUpdateEvent(
            command.getClaimsId(),
            command.getRegistrationDate(),
            command.getUserId(),
            command.getType()));
  }

  @CommandHandler
  public void addDataItem(AddDataItemCommand command) {
    log.info("adding data item to claim");
    DataItemAddedEvent ne = new DataItemAddedEvent();
    ne.setClaimsId(command.getClaimID());
    ne.setDate(command.getDate());
    ne.setId(command.getId());
    ne.setUserId(command.getUserId());

    ne.setName(command.getName());
    ne.setReceived(command.getReceived());
    ne.setTitle(command.getTitle());
    ne.setType(command.getType());
    ne.setValue(command.getValue());

    apply(ne);
  }

  @CommandHandler
  public void addNote(AddNoteCommand command) {
    log.info("adding note to claim");
    NoteAddedEvent ne = new NoteAddedEvent();
    ne.setClaimsId(command.getClaimID());
    ne.setDate(command.getDate());
    ne.setFileURL(command.getFileURL());
    ne.setId(command.getId());
    ne.setText(command.getText());
    ne.setUserId(command.getUserId());
    apply(ne);
  }

  @CommandHandler
  public void addPayment(AddPaymentCommand command) {
    log.info("adding payment to claim");
    PaymentAddedEvent pe = new PaymentAddedEvent();
    pe.setClaimsId(command.getClaimID());
    pe.setDate(command.getDate());
    pe.setId(command.getId());
    pe.setUserId(command.getUserId());

    pe.setAmount(command.getAmount());
    pe.setNote(command.getNote());
    pe.setPayoutDate(command.getPayoutDate());
    pe.setExGratia(command.getExGratia());
    pe.setType(command.getType());
    pe.setHandlerReference(command.getHandlerReference());
    apply(pe);
  }

  @CommandHandler
  public void executePayment(ExecutePaymentCommand cmd) {
    log.info("executing payment to claim {} for member {}", cmd.getClaimId(), cmd.getMemberId());

    PaymentExecutedEvent e = new PaymentExecutedEvent(cmd.getId(), cmd.getClaimId(),
        cmd.getMemberId(), cmd.getAmount(), cmd.getNote(), cmd.isExGracia(),
        cmd.getHandlerReference());

    apply(e);
  }

  // ----------------- Event sourcing --------------------- //

  @EventSourcingHandler
  public void on(ClaimCreatedEvent e) {
    this.id = e.getId();
    this.userId = e.getUserId();
    this.registrationDate = e.getRegistrationDate();
    this.audioURL = e.getAudioURL();

    // Init data structures
    this.notes = new ArrayList<Note>();
    this.payments = new ArrayList<Payment>();
    this.assets = new ArrayList<String>();
    this.data = new ArrayList<>();
  }

  @EventSourcingHandler
  public void on(ClaimStatusUpdatedEvent e) {
    this.state = e.getState();
  }

  @EventSourcingHandler
  public void on(ClaimsReserveUpdateEvent e) {
    this.reserve = e.amount;
  }

  @EventSourcingHandler
  public void on(ClaimsTypeUpdateEvent e) {
    this.type = e.type;
  }

  @EventSourcingHandler
  public void on(DataItemAddedEvent e) {
    DataItem d = new DataItem();
    d.id = e.getId();
    d.date = e.getDate();
    d.userId = e.getUserId();
    d.name = e.getName();
    d.received = e.getReceived();
    d.title = e.getTitle();
    d.type = e.getType();
    d.value = e.getValue();
    data.add(d);
  }

  @EventSourcingHandler
  public void on(PaymentAddedEvent e) {
    Payment p = new Payment();
    p.id = e.getId();
    p.date = e.getDate();
    p.userId = e.getUserId();
    p.amount = e.getAmount();
    p.payoutDate = e.getPayoutDate();
    p.note = e.getNote();
    p.exGratia = e.getExGratia();
    p.type = PaymentType.Manual;
    p.handlerReference = e.getHandlerReference();
    payments.add(p);
  }

  @EventSourcingHandler
  public void on(PaymentExecutedEvent e, @Timestamp Instant timestamp) {
    Payment p = new Payment();
    p.id = e.getId();
    p.date = LocalDateTime.ofInstant(timestamp, ZoneId.of(SWEDEN_TIMEZONE));
    p.userId = e.getMemberId();
    p.amount = e.getAmount().getNumber().doubleValueExact();
    p.payoutDate = LocalDateTime.ofInstant(timestamp, ZoneId.of(SWEDEN_TIMEZONE));
    p.note = e.getNote();
    p.exGratia = e.isExGracia();
    p.type = PaymentType.Automatic;
    p.handlerReference = e.getHandlerReference();
    payments.add(p);
  }

  @EventSourcingHandler
  public void on(NoteAddedEvent e) {
    Note n = new Note();
    n.id = e.getId();
    n.fileURL = e.getFileURL();
    n.text = e.getText();
    n.userId = e.getUserId();
    n.date = e.getDate();
    notes.add(n);
  }
}
