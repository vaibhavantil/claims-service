package com.hedvig.claims.query;

import static com.hedvig.claims.util.TzHelper.SWEDEN_TZ;

import com.hedvig.claims.aggregates.Asset;
import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.aggregates.DataItem;
import com.hedvig.claims.aggregates.Note;
import com.hedvig.claims.aggregates.Payment;
import com.hedvig.claims.aggregates.PayoutStatus;
import com.hedvig.claims.events.AutomaticPaymentAddedEvent;
import com.hedvig.claims.events.AutomaticPaymentFailedEvent;
import com.hedvig.claims.events.AutomaticPaymentInitiatedEvent;
import com.hedvig.claims.events.BackofficeClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import com.hedvig.claims.events.ClaimsTypeUpdateEvent;
import com.hedvig.claims.events.DataItemAddedEvent;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.events.PaymentAddedEvent;
import com.hedvig.claims.web.dto.PaymentType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClaimsEventListener {

  private final ClaimsRepository claimRepository;
  private final PaymentRepository paymentRepository;

  @Autowired
  public ClaimsEventListener(ClaimsRepository claimRepository,
    PaymentRepository paymentRepository) {
    this.claimRepository = claimRepository;
    this.paymentRepository = paymentRepository;
  }

  @EventHandler
  public void on(ClaimCreatedEvent e, @Timestamp Instant timestamp) {
    log.info("ClaimCreatedEvent: " + e);
    ClaimEntity claim = new ClaimEntity();
    claim.id = e.getId();
    claim.userId = e.getUserId();
    claim.registrationDate = timestamp;
    claim.audioURL = e.getAudioURL();
    claim.state = ClaimsAggregate.ClaimStates.OPEN;
    claim.claimSource = ClaimSource.APP;

    // Init data structures
    claim.notes = new HashSet<Note>();
    claim.payments = new HashSet<Payment>();
    claim.assets = new HashSet<Asset>();
    claim.events = new HashSet<Event>();

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = "Claim created";
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventHandler
  public void on(BackofficeClaimCreatedEvent e) {
    log.info("BackofficeClaimCreatedEvent: " + e);
    ClaimEntity claim = new ClaimEntity();
    claim.id = e.getId();
    claim.userId = e.getMemberId();
    claim.registrationDate = e.getRegistrationDate();
    claim.state = ClaimsAggregate.ClaimStates.OPEN;
    claim.claimSource = e.getClaimSource();

    // Init data structures
    claim.notes = new HashSet<Note>();
    claim.payments = new HashSet<Payment>();
    claim.assets = new HashSet<Asset>();
    claim.events = new HashSet<Event>();

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getMemberId();
    ev.text = "Claim created";
    claim.addEvent(ev);

    claimRepository.save(claim);
  }


  @EventHandler
  public void on(NoteAddedEvent e) {
    log.info("NoteAddedEvent: " + e);
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimsId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimsId()));
    Note n = new Note();
    n.id = e.getId();
    n.date = e.getDate();
    n.fileURL = e.getFileURL();
    n.text = e.getText();
    n.userId = e.getUserId();
    claim.addNote(n);

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = "Note added:" + n.text;
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(ClaimStatusUpdatedEvent e) {
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimsId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimsId()));

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = "Status updated from " + claim.state + " to " + e.getState().toString();

    claim.state = e.getState();
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(ClaimsTypeUpdateEvent e) {
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimID())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimID()));

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text =
      "Claim's type "
        + (claim.type == null ? "initialised as " : ("updated from " + claim.type + " to "))
        + e.getType();

    claim.type = e.getType();
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(ClaimsReserveUpdateEvent e) {
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimID())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimID()));

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = "Reserve updated from " + claim.reserve + " to " + e.getAmount();

    claim.reserve = e.getAmount();
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(DataItemAddedEvent e) {
    log.info("DattaItemAddedEvent: " + e);
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimsId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimsId()));

    DataItem d = new DataItem();
    d.id = e.getId();
    d.date = e.getDate();
    d.userId = e.getUserId();
    d.name = e.getName();
    d.received = e.getReceived();
    d.title = e.getTitle();
    d.type = e.getType();
    d.value = e.getValue();
    claim.addDataItem(d);

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = "Data item added. " + d.name + ":" + (d.received == null ? "not" : "") + " received ";
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(PaymentAddedEvent e, @Timestamp Instant timestamp) {
    log.info("PaymentAddedEvent: " + e);
    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimsId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimsId()));
    Payment p = new Payment();
    p.id = e.getId();
    p.date = e.getDate();
    p.userId = e.getUserId();
    p.amount = e.getAmount();
    p.deductible = e.getDeductible();
    p.payoutDate = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
    p.note = e.getNote();
    p.exGratia = e.getExGratia();
    p.type = PaymentType.Manual;
    p.handlerReference = e.getHandlerReference();
    p.payoutStatus = PayoutStatus.COMPLETED;
    claim.addPayment(p);

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getUserId();
    ev.text = String.format(
      "A manual payment (%s) was executed. The amount is %s \nThe payment was added by %s on %s",
      p.id, p.amount.toString(), p.handlerReference, p.payoutDate.toString());
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentAddedEvent e, @Timestamp Instant timestamp) {
    log.info("PaymentExecutedEvent: {}" + e);

    ClaimEntity claim =
      claimRepository
        .findById(e.getClaimId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + e.getClaimId()));
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
    claim.addPayment(p);

    Event ev = new Event();
    ev.type = e.getClass().getName();
    ev.userId = e.getMemberId();
    ev.text = String.format(
      "An automatic payment (%s) was executed. The amount is %s \nThe payment was initiated by %s on %s",
      p.id,p.amount.toString(), p.handlerReference, p.payoutDate.toString());
    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentInitiatedEvent e, @Timestamp Instant timestamp) {
    log.info("PaymentInitiatedEvent: {}" + e);

    Optional<Payment> optionalPayment = paymentRepository.findById(e.getId());

    if (!optionalPayment.isPresent()) {
      log.error("PaymentInitiatedEvent - Cannot find payment with id {} for claim {}", e.getId(),
        e.getClaimId());
    } else {
      Payment payment = optionalPayment.get();

      payment.payoutStatus = PayoutStatus.INITIATED;
      payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);
      payment.payoutReference = e.getTransactionReference();

      paymentRepository.save(payment);

      ClaimEntity claim =
        claimRepository
          .findById(e.getClaimId())
          .orElseThrow(
            () ->
              new ResourceNotFoundException(
                "Could not find claim with id:" + e.getClaimId()));

      Event ev = new Event();
      ev.type = e.getClass().getName();
      ev.userId = e.getMemberId();
      ev.text = String.format(
        "An automatic payment (%s) with the amount %s was successfully initiated.\nThe payment was initiated by %s with referenceId %s",
        payment.id, payment.amount.toString(), payment.handlerReference, payment.payoutReference);
      claim.addEvent(ev);

      claimRepository.save(claim);
    }
  }

  @EventSourcingHandler
  public void on(AutomaticPaymentFailedEvent e, @Timestamp Instant timestamp) {
    log.info("PayoutFailedEvent: {}" + e);

    Optional<Payment> optionalPayment = paymentRepository.findById(e.getId());

    if (!optionalPayment.isPresent()) {
      log.error("PaymentInitiatedEvent - Cannot find payment with id {} for claim {}", e.getId(),
        e.getClaimId());
    } else {
      Payment payment = optionalPayment.get();

      payment.payoutStatus = PayoutStatus.parseToPayoutStatus(e.getTransactionStatus());
      payment.date = LocalDateTime.ofInstant(timestamp, SWEDEN_TZ);

      paymentRepository.save(payment);

      ClaimEntity claim =
        claimRepository
          .findById(e.getClaimId())
          .orElseThrow(
            () ->
              new ResourceNotFoundException(
                "Could not find claim with id:" + e.getClaimId()));

      Event ev = new Event();
      ev.type = e.getClass().getName();
      ev.userId = e.getMemberId();
      ev.text = String.format(
        "An automatic payment (%s) with the amount %s failed!\nThe payment was initiated by %s with referenceId %s",
        payment.id, payment.amount.toString(), payment.handlerReference, payment.payoutReference);
      claim.addEvent(ev);

      claimRepository.save(claim);

    }
  }
}
