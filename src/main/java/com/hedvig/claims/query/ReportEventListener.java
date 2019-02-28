package com.hedvig.claims.query;

import com.hedvig.claims.events.*;
import com.hedvig.claims.services.ReportGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ReplayStatus;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

@Slf4j
@Component
@ProcessingGroup("report")
public class ReportEventListener {

  private ClaimReportRepository claimReportRepository;
  private ReportGenerationService reportGenerationService;
  private EventStore eventStore;
  private static String DATE = "DATE";
  private static String Z = "Z";
  private static String SEK = "SEK";


  public ReportEventListener(ClaimReportRepository claimReportRepository, ReportGenerationService reportGenerationService, EventStore eventStore) {
    this.claimReportRepository = claimReportRepository;
    this.reportGenerationService = reportGenerationService;
    this.eventStore = eventStore;
  }

  @EventHandler
  public void on(ClaimCreatedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      claimReportRepository.save(
        new ClaimReportEntity(
          e.getId(),
          e.getUserId(),
          timestamp.atZone(ZoneId.of("Europe/Stockholm")).toLocalDate(),
          "OPEN",
          timestamp.atZone(ZoneId.of("Europe/Stockholm")).toLocalDate(),
          false
        )
      );
    }
  }

  @EventHandler
  public void on(BackofficeClaimCreatedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      claimReportRepository.save(
        new ClaimReportEntity(
          e.getId(),
          e.getMemberId(),
          e.getRegistrationDate().atZone(ZoneId.of("Europe/Stockholm")).toLocalDate(),
          "OPEN",
          e.getRegistrationDate().atZone(ZoneId.of("Europe/Stockholm")).toLocalDate(),
          false
        )
      );
    }
  }


  @EventHandler
  public void on(ClaimStatusUpdatedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.getClaimsId());

      claim.setClaimStatus(e.getState().toString());
      claim.setClaimStatusLastUpdated(LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Stockholm")).toLocalDate());

      claimReportRepository.save(claim);
    }
  }

  @EventHandler
  public void on(ClaimsTypeUpdateEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.claimID);

      claim.setDescriptionOfLoss(e.getType());
      claimReportRepository.save(claim);
    }
  }

  @EventHandler
  public void on(ClaimsReserveUpdateEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.claimID);

      claim.setReserved(BigDecimal.valueOf(e.amount));
      claim.setCurrency(SEK);
      claimReportRepository.save(claim);
    }
  }

  @EventHandler
  public void on(DataItemAddedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.getClaimsId());
      if (e.getName().equalsIgnoreCase(DATE)) {
        if (e.getValue().toUpperCase().contains(Z)) {
          LocalDate dateOfLoss = LocalDateTime.ofInstant(Instant.parse(e.getValue()), ZoneId.of("Europe/Stockholm")).toLocalDate();
          claim.setDateOfLoss(dateOfLoss);
          claim.setClaimYear(dateOfLoss.getYear());
          claimReportRepository.save(claim);
        } else {
          LocalDate dateOfLoss = LocalDateTime.parse(e.getValue()).toLocalDate();
          claim.setDateOfLoss(dateOfLoss);
          claim.setClaimYear(dateOfLoss.getYear());
          claimReportRepository.save(claim);
        }
      }
    }
  }

  @EventHandler
  public void on(AutomaticPaymentInitiatedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.getClaimId());

      Optional<AutomaticPaymentAddedEvent> optionalAutomaticPaymentAddedEvent = eventStore.readEvents(e.getClaimId()).asStream()
        .filter(x -> x.getPayloadType().getTypeName().equalsIgnoreCase(AutomaticPaymentAddedEvent.class.getTypeName()))
        .map(x -> (AutomaticPaymentAddedEvent) x.getPayload())
        .filter(x -> x.getId().equalsIgnoreCase(e.getId())).findFirst();

      if (optionalAutomaticPaymentAddedEvent.isPresent()) {
        claim.setCurrency(SEK);
        claim.setGrossPaid((claim.getGrossPaid() == null ? BigDecimal.ZERO : claim.getGrossPaid())
          .add(BigDecimal.valueOf(optionalAutomaticPaymentAddedEvent.get().getAmount().getNumber().doubleValueExact())));
        claimReportRepository.save(claim);
      }
    }
  }

  @EventHandler
  public void on(EmployeeClaimStatusUpdatedEvent e, @Timestamp Instant timestamp, ReplayStatus replayStatus) {
    if (replayStatus.isReplay() && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
      ClaimReportEntity claim = getClaimReportEntity(e.getClaimId());
      claim.setCoveringEmployee(e.isCoveringEmployee());
      claimReportRepository.save(claim);
    }
  }

  @ResetHandler
  public void onReset() {
    log.info("Deleting Database");
    claimReportRepository.deleteAll();
  }

  private boolean isBeforePeriod(YearMonth yearMonth, Instant timestamp) {
    return timestamp.isBefore(
      yearMonth.atEndOfMonth()
        .atTime(LocalTime.MAX).toInstant(ZoneId.of("Europe/Stockholm").getRules().getOffset(Instant.now()))
    );
  }

  private ClaimReportEntity getClaimReportEntity(String claimId) {
    Optional<ClaimReportEntity> optionalClaim = claimReportRepository.findById(claimId);
    if (!optionalClaim.isPresent()) {
      log.error("Claim $claimId cannot be found in the claimReportRepository.");
      throw new RuntimeException("Claim $claimId cannot be found in the claimReportRepository");
    }
    return optionalClaim.get();
  }
}
