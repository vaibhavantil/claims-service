package com.hedvig.claims.query;

import com.hedvig.claims.events.*;
import com.hedvig.claims.web.dto.ClaimDataType;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component
@Slf4j
@ProcessingGroup("liveReporting")
public class ClaimReportHistoryEventListener {
  private static final String EUROPE_STOCKHOLM = "Europe/Stockholm";
  private static final String OPEN = "OPEN";
  private static final String CLOSED = "CLOSED";
  private ClaimReportHistoryRepository claimReportHistoryRepository;
  private EventStore eventStore;
  private static String Z = "Z";
  private static String SEK = "SEK";
  private final Set<String> EX_GRACIA_TO_INCLUDE_ALWAYS  = Set.of(
    "71f3cf43-c4b2-488a-8b9f-74d8c4ceed79"
  );


  public ClaimReportHistoryEventListener(ClaimReportHistoryRepository claimReportHistoryRepository, EventStore eventStore) {
    this.claimReportHistoryRepository = claimReportHistoryRepository;
    this.eventStore = eventStore;
  }

  @EventHandler
  public void on(ClaimCreatedEvent e, @Timestamp Instant timestamp) {
    log.info("Claim {} created", e.getId());
    claimReportHistoryRepository.save(
      new ClaimReportHistoryEntity(
        e.getId(),
        e.getUserId(),
        timestamp.atZone(ZoneId.of(EUROPE_STOCKHOLM)).toLocalDate(),
        timestamp.atZone(ZoneId.of(EUROPE_STOCKHOLM)).toLocalDate(),
        OPEN,
        false,
        timestamp
      )
    );
  }

  @EventHandler
  public void on(BackofficeClaimCreatedEvent e, @Timestamp Instant timestamp) {
    log.info("Claim {} created from backoffice", e.getId());

    claimReportHistoryRepository.save(
      new ClaimReportHistoryEntity(
        e.getId(),
        e.getMemberId(),
        e.getRegistrationDate().atZone(ZoneId.of(EUROPE_STOCKHOLM)).toLocalDate(),
        e.getRegistrationDate().atZone(ZoneId.of(EUROPE_STOCKHOLM)).toLocalDate(),
        OPEN,
        false,
        timestamp
      )
    );
  }


  @EventHandler
  public void on(ClaimStatusUpdatedEvent e, @Timestamp Instant timestamp) {
    log.info("Claim {} status updated, changed to {} ", e.getClaimsId(), e.getState());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimsId(), timestamp);

    claimHistoryEntry.setClaimStatus(e.getState().toString());

    claimReportHistoryRepository.save(claimHistoryEntry);
  }

  @EventHandler
  public void on(ClaimsTypeUpdateEvent e, @Timestamp Instant timestamp) {
    log.info("Claim {} type updated to {}", e.getClaimID(), e.getType());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.claimID, timestamp);

    claimHistoryEntry.setDescriptionOfLoss(e.getType());
    claimReportHistoryRepository.save(claimHistoryEntry);
  }

  @EventHandler
  public void on(ClaimsReserveUpdateEvent e, @Timestamp Instant timestamp) {
    log.info("Claim {} reserve updated to {}", e.getClaimID(), e.getAmount());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimID(), timestamp);

    claimHistoryEntry.setReserved(BigDecimal.valueOf(e.amount));
    claimHistoryEntry.setCurrency(SEK);

    final Instant timeOfKnowledgeAdjustedForRaceCondition = claimHistoryEntry.getTimeOfKnowledge().plusMillis(1);
    claimHistoryEntry.setTimeOfKnowledge(timeOfKnowledgeAdjustedForRaceCondition); // Until we meet again, missing-reserves-bug
    claimReportHistoryRepository.save(claimHistoryEntry);
  }

  @EventHandler
  public void on(DataItemAddedEvent e, @Timestamp Instant timestamp) {
    log.info("Data item added to claim {}", e.getClaimsId());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimsId(), timestamp);

    if (e.getType().equals(ClaimDataType.DataType.DATE)) {
      if (e.getValue().toUpperCase().contains(Z)) {
        LocalDate dateOfLoss = LocalDateTime
          .ofInstant(Instant.parse(e.getValue()), ZoneId.of(EUROPE_STOCKHOLM))
          .toLocalDate();
        updateDateOfLoss(claimHistoryEntry, dateOfLoss);
      } else {
        updateDateOfLoss(claimHistoryEntry, LocalDateTime.parse(e.getValue()).toLocalDate());
      }
      claimReportHistoryRepository.save(claimHistoryEntry);
    } else if (claimHistoryEntry.getDateOfLoss() != null) {
      updateDateOfLoss(claimHistoryEntry, claimHistoryEntry.getNotificationDate());
      claimReportHistoryRepository.save(claimHistoryEntry);
    }
  }

  @EventHandler
  public void on(PaymentAddedEvent e, @Timestamp Instant timestamp) {
    log.info("Payment added for claim {}", e.getClaimsId());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimsId(), timestamp);
    if (!e.getExGratia() || EX_GRACIA_TO_INCLUDE_ALWAYS.contains(e.getClaimsId())) {
      claimHistoryEntry.setCurrency(SEK);
      claimHistoryEntry.setGrossPaid((claimHistoryEntry.getGrossPaid() == null ? BigDecimal.ZERO : claimHistoryEntry.getGrossPaid())
        .add(BigDecimal.valueOf(e.getAmount())));

      claimReportHistoryRepository.save(claimHistoryEntry);
    }
  }

  @EventHandler
  public void on(AutomaticPaymentInitiatedEvent e, @Timestamp Instant timestamp) {
    log.info("Automatic payment initiated for claim {}", e.getClaimId());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimId(), timestamp);

    Optional<AutomaticPaymentAddedEvent> optionalAutomaticPaymentAddedEvent = eventStore
      .readEvents(e.getClaimId()).asStream()
      .filter(domainEventMessage -> domainEventMessage.getPayloadType().getTypeName().equalsIgnoreCase(AutomaticPaymentAddedEvent.class.getTypeName()))
      .map(event -> (AutomaticPaymentAddedEvent) event.getPayload())
      .filter(event -> event.getId().equalsIgnoreCase(e.getId()))
      .findFirst();

    if (optionalAutomaticPaymentAddedEvent.isPresent()) {
      AutomaticPaymentAddedEvent automaticPaymentAddedEvent = optionalAutomaticPaymentAddedEvent.get();

      if (!automaticPaymentAddedEvent.isExGracia() || EX_GRACIA_TO_INCLUDE_ALWAYS.contains(e.getClaimId())) {
        claimHistoryEntry.setCurrency(SEK);
        claimHistoryEntry.setGrossPaid((claimHistoryEntry.getGrossPaid() == null ? BigDecimal.ZERO : claimHistoryEntry.getGrossPaid())
          .add(BigDecimal.valueOf(automaticPaymentAddedEvent.getAmount().getNumber().doubleValueExact())));

        claimReportHistoryRepository.save(claimHistoryEntry);
      }
    }
  }

  @EventHandler
  public void on(EmployeeClaimStatusUpdatedEvent e, @Timestamp Instant timestamp) {
    log.info("Employee claim status updated for claim {}", e.getClaimId());

    ClaimReportHistoryEntity claimHistoryEntry = copyLatestClaimHistoryEntity(e.getClaimId(), timestamp);

    claimHistoryEntry.setCoveringEmployee(e.isCoveringEmployee());
    claimReportHistoryRepository.save(claimHistoryEntry);
  }

  @ResetHandler
  public void onReset() {
    log.warn("Deleting all claim report history entities");
    claimReportHistoryRepository.deleteAll();
  }

  private ClaimReportHistoryEntity copyLatestClaimHistoryEntity(String claimId, Instant timeOfKnowledge) {
    List<ClaimReportHistoryEntity> listOfClaims = claimReportHistoryRepository.findByClaimId(claimId);

    Optional<ClaimReportHistoryEntity> claimReportHistoryEntityMaybe = listOfClaims.stream()
      .filter(claimReportHistoryEntity -> !claimReportHistoryEntity.getTimeOfKnowledge().isAfter(timeOfKnowledge))
      .max(Comparator.comparing(ClaimReportHistoryEntity::getTimeOfKnowledge));

    if (!claimReportHistoryEntityMaybe.isPresent()) {
      log.error("Claim {} cannot be found in the claimReportHistoryRepository.", claimId);
      throw new RuntimeException("Claim cannot be found in the claimReportHistoryRepository");
    }

    return ClaimReportHistoryEntity.copy(claimReportHistoryEntityMaybe.get(), timeOfKnowledge);
  }

  private void updateDateOfLoss(ClaimReportHistoryEntity updatedClaimHistoryEntry, LocalDate dateOfLoss) {
    updatedClaimHistoryEntry.setDateOfLoss(dateOfLoss);
    updatedClaimHistoryEntry.setClaimYear(dateOfLoss.getYear());
  }
}
