package com.hedvig.claims.aggregates;

import com.hedvig.claims.commands.*;
import com.hedvig.claims.events.*;
import com.hedvig.claims.web.dto.PaymentType;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.hedvig.claims.util.TzHelper.SWEDEN_TZ;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@Slf4j
public class ClaimsAggregate {

  public enum ClaimStates {
    OPEN,
    CLOSED,
    REOPENED
  }

  @AggregateIdentifier
  public String id;

  public String userId;
  public String audioURL;
  public Instant registrationDate;
  public ClaimStates state;
  public Double reserve;
  public String type;
  public ClaimSource claimSource;

  public ArrayList<DataItem> data;
  public HashMap<String, Payment> payments;
  public ArrayList<Note> notes;
  public ArrayList<String> assets;

  public boolean isCoveringEmployee;

  public ClaimsAggregate() {
    log.info("Instantiate ClaimsAggregate");
  }

  @CommandHandler
  public ClaimsAggregate(CreateClaimCommand command) {
    log.info("create claim");
    apply(
      new ClaimCreatedEvent(
        command.getId(),
        command.getUserId(),
        command.getAudioURL()));
  }

  @CommandHandler
  public ClaimsAggregate(CreateBackofficeClaimCommand command) {
    log.info("create claim");
    apply(
      new BackofficeClaimCreatedEvent(
        command.getId(),
        command.getMemberId(),
        command.getRegistrationDate(),
        command.getClaimSource()));
  }

  @CommandHandler
  public void update(UpdateClaimsStateCommand command) {
    log.info("update claim state");
    apply(
      new ClaimStatusUpdatedEvent(
        command.getClaimsId(),
        this.userId,
        command.getRegistrationDate(),
        command.getState()));

    if (command.getState() == ClaimStates.CLOSED) {
      apply(
        new ClaimsReserveUpdateEvent(
          command.getClaimsId(),
          LocalDateTime.now(),
          this.userId,
          0.0));
    }
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
        this.userId,
        command.getType()));
  }

  @CommandHandler
  public void addDataItem(AddDataItemCommand command) {
    log.info("adding data item to claim");
    DataItemAddedEvent ne = new DataItemAddedEvent(
      command.getId(),
      command.getClaimID(),
      command.getDate(),
      this.userId,
      command.getType(),
      command.getName(),
      command.getTitle(),
      command.getReceived(),
      command.getValue()
    );
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
    ne.setUserId(this.userId);
    apply(ne);
  }

  @CommandHandler
  public void addPayment(AddPaymentCommand cmd) {
    log.info("adding payment to claim");
    PaymentAddedEvent pe = new PaymentAddedEvent(
      cmd.getId(),
      cmd.getClaimID(),
      cmd.getDate(),
      this.userId,
      cmd.getAmount(),
      cmd.getDeductible(),
      cmd.getNote(),
      cmd.getPayoutDate(),
      cmd.getExGratia(),
      cmd.getHandlerReference()
    );
    apply(pe);
  }

  @CommandHandler
  public void addAutomaticPayment(AddAutomaticPaymentCommand cmd) {
    log.info("add automatic payment to claim {} for member {}", cmd.getClaimId(),
      cmd.getMemberId());

    AutomaticPaymentAddedEvent e = new AutomaticPaymentAddedEvent(
      UUID.randomUUID().toString(),
      cmd.getClaimId(),
      this.userId,
      cmd.getAmount(),
      cmd.getDeductible(),
      cmd.getNote(),
      cmd.isExGracia(),
      cmd.getHandlerReference(),
      cmd.isSanctionCheckSkipped());

    apply(e);
  }

  @CommandHandler
  public void addInitiatedAutomaticPayment(AddInitiatedAutomaticPaymentCommand cmd) {
    log.info("add initiated automatic payment to member {} for claim {}", cmd.getMemberId(),
      cmd.getClaimId());

    AutomaticPaymentInitiatedEvent e = new AutomaticPaymentInitiatedEvent(
      cmd.getId(),
      cmd.getClaimId(),
      this.userId,
      cmd.getTransactionReference(),
      cmd.getTransactionStatus());

    apply(e);
  }

  @CommandHandler
  public void addFailedAutomaticPayment(AddFailedAutomaticPaymentCommand cmd) {
    log.info("payment failed to be processed to member {} for claim {}", cmd.getMemberId(),
      cmd.getClaimId());

    AutomaticPaymentFailedEvent e = new AutomaticPaymentFailedEvent(
      cmd.getId(),
      cmd.getClaimId(),
      this.userId,
      cmd.getTransactionStatus());

    apply(e);
  }

  @CommandHandler
  public void on(UpdateEmployeeClaimStatusCommand cmd) {
    apply(new EmployeeClaimStatusUpdatedEvent(cmd.getClaimId(), cmd.isCoveringEmployee()));
  }

  @CommandHandler
  public void on(UploadClaimFileCommand cmd) {
    apply(new ClaimFileUploadedEvent(cmd.getClaimFileId(), cmd.getBucket(), cmd.getKey(), cmd.getClaimId(), cmd.getContentType(), cmd.getData(), cmd.getFileName(),
      cmd.getImageId(), cmd.getMetaInfo(), cmd.getSize(), cmd.getUserId())
    );
  }

  @CommandHandler
  public void on (DeleteClaimFileCommand cmd) {
    apply(new DeleteClaimFileEvent(cmd.getClaimFileId(), cmd.getDeletedBy(), Instant.now()));
  }

  // ----------------- Event sourcing --------------------- //

  @EventSourcingHandler
  public void on(ClaimCreatedEvent e, @Timestamp Instant timestamp) {
    this.id = e.getId();
    this.userId = e.getUserId();
    this.registrationDate = timestamp;
    this.audioURL = e.getAudioURL();

    // Init data structures
    this.notes = new ArrayList<Note>();
    this.payments = new HashMap<>();
    this.assets = new ArrayList<String>();
    this.data = new ArrayList<>();
    this.claimSource = ClaimSource.APP;

    this.isCoveringEmployee = false;
  }

  @EventSourcingHandler
  public void on(BackofficeClaimCreatedEvent e) {
    this.id = e.getId();
    this.userId = e.getMemberId();
    this.registrationDate = e.getRegistrationDate();
    this.claimSource = e.getClaimSource();

    // Init data structures
    this.notes = new ArrayList<Note>();
    this.payments = new HashMap<>();
    this.assets = new ArrayList<String>();
    this.data = new ArrayList<>();

    this.isCoveringEmployee = false;
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
    p.deductible = e.getDeductible();
    p.payoutDate = e.getPayoutDate();
    p.note = e.getNote();
    p.exGratia = e.getExGratia();
    p.type = PaymentType.Manual;
    p.handlerReference = e.getHandlerReference();
    p.payoutStatus = PayoutStatus.COMPLETED;
    payments.put(e.getId(), p);
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentAddedEvent e, @Timestamp Instant timestamp) {
    Payment p = new Payment();
    p.id = e.getId();
    p.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
    p.userId = e.getMemberId();
    p.amount = e.getAmount().getNumber().doubleValueExact();
    p.deductible = e.getDeductible().getNumber().doubleValueExact();
    p.payoutDate = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
    p.note = e.getNote();
    p.exGratia = e.isExGracia();
    p.type = PaymentType.Automatic;
    p.handlerReference = e.getHandlerReference();
    p.payoutStatus = PayoutStatus.PREPARED;
    payments.put(e.getId(), p);
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentInitiatedEvent e, @Timestamp Instant timestamp) {
    if (!payments.containsKey(e.getId())) {
      log.error("AutomaticPaymentInitiatedEvent - Cannot find payment with id {} for claim {}",
        e.getId(),
        e.getClaimId());
    } else {
      Payment payment = payments.get(e.getId());

      payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
      payment.payoutReference = e.getTransactionReference();
      payment.payoutStatus = PayoutStatus.INITIATED;

      payments.put(e.getId(), payment);
    }
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentFailedEvent e, @Timestamp Instant timestamp) {
    if (!payments.containsKey(e.getId())) {
      log.error("AutomaticPaymentFailedEvent - Cannot find payment with id {} for claim {}",
        e.getId(),
        e.getClaimId());
    } else {
      Payment payment = payments.get(e.getId());

      payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
      payment.payoutStatus = PayoutStatus.parseToPayoutStatus(e.getTransactionStatus());

      payments.put(e.getId(), payment);
    }
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

  @EventSourcingHandler
  public void on(EmployeeClaimStatusUpdatedEvent e) {
    this.isCoveringEmployee = e.isCoveringEmployee();
  }

}
