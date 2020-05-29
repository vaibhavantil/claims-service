package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.*;
import com.hedvig.claims.events.*;
import com.hedvig.claims.web.dto.PaymentType;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static com.hedvig.claims.util.TzHelper.SWEDEN_TZ;

@Component
@Slf4j
public class ClaimsEventListener {

  private final ClaimsRepository claimRepository;
  private final PaymentRepository paymentRepository;
  private final ClaimFileRepository claimFileRepository;

  @Autowired
  public ClaimsEventListener(
    ClaimsRepository claimRepository,
    PaymentRepository paymentRepository,
    ClaimFileRepository claimFileRepository) {
    this.claimRepository = claimRepository;
    this.paymentRepository = paymentRepository;
    this.claimFileRepository = claimFileRepository;
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
    claim.coveringEmployee = false;

    // Init data structures
    claim.notes = new HashSet<Note>();
    claim.payments = new HashSet<Payment>();
    claim.assets = new HashSet<Asset>();
    claim.events = new HashSet<Event>();
    claim.transcriptions = new HashSet<>();

    Event ev = createEvent(e, "Claim created");
    ev.userId = e.getUserId();
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
    claim.coveringEmployee = false;

    // Init data structures
    claim.notes = new HashSet<Note>();
    claim.payments = new HashSet<Payment>();
    claim.assets = new HashSet<Asset>();
    claim.events = new HashSet<Event>();

    Event ev = createEvent(e, "Claim created");
    ev.userId = e.getMemberId();

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

    Event ev = createEvent(e, "Note added:" + n.text);
    ev.userId = e.getUserId();

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

    Event ev = createEvent(e, "Status updated from " + claim.state + " to " + e.getState().toString());
    ev.userId = e.getUserId();

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

    Event ev = createEvent(e,
          "Claim's type "
        + (claim.type == null ? "initialised as " : ("updated from " + claim.type + " to "))
        + e.getType());
    ev.userId = e.getUserId();

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

    Event ev = createEvent(e, "Reserve updated from " + claim.reserve + " to " + e.getAmount());
    ev.userId = e.getUserId();

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

    Event ev = createEvent(e, "Data item added. " + d.name + ":" + (d.received == null ? "not" : "") + " received ");
    ev.userId = e.getUserId();
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

    Event ev = createEvent(e, String.format(
      "A manual payment (%s) was executed. The amount is %s \nThe payment was added by %s on %s",
      p.id, p.amount.toString(), p.handlerReference, p.payoutDate.toString()));
    ev.userId = e.getUserId();

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

    Event ev = createEvent(e, String.format(
      "An automatic payment (%s) was executed. The amount is %s \nThe payment was initiated by %s on %s",
      p.id, p.amount.toString(), p.handlerReference, p.payoutDate.toString()));
    ev.userId = e.getMemberId();

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

      Event ev = createEvent(e, String.format(
        "An automatic payment (%s) with the amount %s was successfully initiated.\nThe payment was initiated by %s with referenceId %s",
        payment.id, payment.amount.toString(), payment.handlerReference, payment.payoutReference));
      ev.userId = e.getMemberId();

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

      Event ev = createEvent(e, String.format(
        "An automatic payment (%s) with the amount %s failed!\nThe payment was initiated by %s with referenceId %s",
        payment.id, payment.amount.toString(), payment.handlerReference, payment.payoutReference));
      ev.userId = e.getMemberId();

      claim.addEvent(ev);

      claimRepository.save(claim);

    }
  }

  @EventHandler
  public void on(EmployeeClaimStatusUpdatedEvent e) {
    Optional<ClaimEntity> optionalClaim = claimRepository.findById(e.getClaimId());
    if (!optionalClaim.isPresent()) {
      log.error("EmployeeClaimSetEvent - Cannot find the claim");
    } else {
      ClaimEntity claim = optionalClaim.get();
      claim.coveringEmployee = e.isCoveringEmployee();

      Event ev = createEvent(e, e.isCoveringEmployee() ? "Claim marked as an employee's claim" : "Claim was marked as a regular claim");
      ev.userId = claim.userId;
      claim.addEvent(ev);

      claimRepository.save(claim);
    }
  }

  @EventHandler
  public void on(ClaimFileUploadedEvent event) {
      ClaimEntity claim = findClaimOrThrowException(event.getClaimId());
      ClaimFile claimFile = new ClaimFile();

      claimFile.setId(event.getClaimFileId());
      claimFile.setBucket(event.getBucket());
      claimFile.setKey(event.getKey());
      claimFile.setContentType(event.getContentType());
      claimFile.setFileName(event.getFileName());
      claimFile.setUploadedAt(event.getUploadedAt());
      claimFile.setUploadSource(event.getUploadSource());
      claim.addClaimFile(claimFile);

      Event ev = createEvent(event, String.format(
        "A claim file was uploaded with id %s at %s.",
        event.getClaimFileId(), event.getUploadedAt()));
        claim.addEvent(ev);

      claimRepository.save(claim);
  }

  @EventHandler
  public void on(ClaimFileMarkedAsDeletedEvent event) {
    ClaimEntity claim = findClaimOrThrowException(event.getClaimId());
    ClaimFile claimFile = findClaimFileOrThrowException(event.getClaimFileId(), claim);

    claimFile.setMarkedAsDeleted(true);
    claimFile.setMarkedAsDeletedAt(event.getDeletedAt());
    claimFile.setMarkedAsDeletedBy(event.getDeletedBy());
    claimFileRepository.save(claimFile);

    Event ev = createEvent(event, String.format(
      "A claim file with id %s was deleted by %s at %s",
      claimFile.getId(), event.getDeletedBy(), event.getDeletedAt()));

    claim.addEvent(ev);

    claimRepository.save(claim);
  }

  @EventHandler
  public void on(ClaimFileCategorySetEvent event) {
    ClaimFile file = claimFileRepository.findById(event.getClaimFileId()).get();
    file.setCategory(event.getCategory());
    claimFileRepository.save(file);
  }

  @EventHandler
  public void on(AudioTranscribedEvent event) {
      Optional<ClaimEntity> entity = claimRepository.findById(event.getClaimId());
      if(entity.isPresent()) {
          val e = entity.get();
          e.transcriptions.add(
              new Transcription(
                  UUID.randomUUID().toString(),
                  event.getText(),
                  event.getConfidence(),
                  event.getLanguageCode()
              )
          );

          Event ev = createEvent(event, String.format(
              "Homer created a new transcription with the confidence of %s. Transcription: %s language code: %s",
              event.getConfidence(), event.getText(), event.getLanguageCode()));
          e.addEvent(ev);

          claimRepository.save(e);
      }
  }

    @NotNull
    private Event createEvent(Object event, String message) {
        Event ev = new Event();
        ev.type = event.getClass().getName();
        ev.text = message;
        return ev;
    }

    private ClaimEntity findClaimOrThrowException(String claimId) {
    ClaimEntity claim =
      claimRepository
        .findById(claimId)
        .orElseThrow(
          () ->
            new ResourceNotFoundException(
              "Could not find claim with id:" + claimId));
    return claim;
  }

  private ClaimFile findClaimFileOrThrowException(UUID claimFileId, ClaimEntity claim) {

    val claimFileMaybe = claim.claimFiles.stream()
      .filter(claimFile -> claimFile.getId().equals(claimFileId)).findAny();

    if (!claimFileMaybe.isPresent()) {
      throw new RuntimeException(
        "no claim file can be found with id " + claimFileId + "for claim " + claim.id);
    }
    return claimFileMaybe.get();
  }
}
