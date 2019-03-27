package com.hedvig.claims.query;

import com.hedvig.claims.events.*;
import com.hedvig.claims.web.dto.ClaimDataType;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
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

@Component
@Slf4j
@ProcessingGroup("liveReporting")
public class ClaimReportHistoryEventListener {

  private static final String EUROPE_STOCKHOLM = "Europe/Stockholm";
  private static final String OPEN = "OPEN";
  private ClaimReportHistoryRepository claimReportHistoryRepository;
  private EventStore eventStore;
  private static String DATE = "DATE";
  private static String Z = "Z";
  private static String SEK = "SEK";


  public ClaimReportHistoryEventListener(ClaimReportHistoryRepository claimReportHistoryRepository, EventStore eventStore) {
    this.claimReportHistoryRepository = claimReportHistoryRepository;
    this.eventStore = eventStore;
  }

  @EventHandler
  public void on(ClaimCreatedEvent e, @Timestamp Instant timestamp) {
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
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.getClaimsId(), timestamp);

    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    updatedClaimHistoryEntry.setClaimStatus(e.getState().toString());

    claimReportHistoryRepository.save(updatedClaimHistoryEntry);
  }

  @EventHandler
  public void on(ClaimsTypeUpdateEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.claimID, timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    updatedClaimHistoryEntry.setDescriptionOfLoss(e.getType());
    claimReportHistoryRepository.save(updatedClaimHistoryEntry);
  }

  @EventHandler
  public void on(ClaimsReserveUpdateEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.claimID, timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    updatedClaimHistoryEntry.setReserved(BigDecimal.valueOf(e.amount));
    updatedClaimHistoryEntry.setCurrency(SEK);
    claimReportHistoryRepository.save(updatedClaimHistoryEntry);
  }

  @EventHandler
  public void on(DataItemAddedEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.getClaimsId(), timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    if (e.getType().equals(ClaimDataType.DataType.DATE)) {
      if (e.getValue().toUpperCase().contains(Z)) {
        LocalDate dateOfLoss = LocalDateTime.ofInstant(Instant.parse(e.getValue()), ZoneId.of(EUROPE_STOCKHOLM)).toLocalDate();
        updatedClaimHistoryEntry.setDateOfLoss(dateOfLoss);
        updatedClaimHistoryEntry.setClaimYear(dateOfLoss.getYear());
        claimReportHistoryRepository.save(updatedClaimHistoryEntry);
      } else {
        LocalDate dateOfLoss = LocalDateTime.parse(e.getValue()).toLocalDate();
        updatedClaimHistoryEntry.setDateOfLoss(dateOfLoss);
        updatedClaimHistoryEntry.setClaimYear(dateOfLoss.getYear());
        claimReportHistoryRepository.save(updatedClaimHistoryEntry);
      }
    } else {
      updatedClaimHistoryEntry.setDateOfLoss(recentClaimHistoryEntry.getNotificationDate());
      claimReportHistoryRepository.save(updatedClaimHistoryEntry);
    }
  }

  @EventHandler
  public void on(PaymentAddedEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.getClaimsId(), timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    updatedClaimHistoryEntry.setCurrency(SEK);

    updatedClaimHistoryEntry.setGrossPaid((updatedClaimHistoryEntry.getGrossPaid() == null ? BigDecimal.ZERO : updatedClaimHistoryEntry.getGrossPaid())
      .add(BigDecimal.valueOf(e.getAmount())));
    claimReportHistoryRepository.save(updatedClaimHistoryEntry);
  }

  @EventHandler
  public void on(AutomaticPaymentInitiatedEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.getClaimId(), timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    Optional<AutomaticPaymentAddedEvent> optionalAutomaticPaymentAddedEvent = eventStore
      .readEvents(e.getClaimId()).asStream()
      .filter(domainEventMessage -> domainEventMessage.getPayloadType().getTypeName().equalsIgnoreCase(AutomaticPaymentAddedEvent.class.getTypeName()))
      .map(event -> (AutomaticPaymentAddedEvent) event.getPayload())
      .filter(event -> event.getId().equalsIgnoreCase(e.getId()))
      .findFirst();

    if (optionalAutomaticPaymentAddedEvent.isPresent()) {
      updatedClaimHistoryEntry.setCurrency(SEK);
      updatedClaimHistoryEntry.setGrossPaid((updatedClaimHistoryEntry.getGrossPaid() == null ? BigDecimal.ZERO : updatedClaimHistoryEntry.getGrossPaid())
        .add(BigDecimal.valueOf(optionalAutomaticPaymentAddedEvent.get().getAmount().getNumber().doubleValueExact())));
      claimReportHistoryRepository.save(updatedClaimHistoryEntry);
    }
  }

  @EventHandler
  public void on(EmployeeClaimStatusUpdatedEvent e, @Timestamp Instant timestamp) {
    ClaimReportHistoryEntity recentClaimHistoryEntry = getClaimReportHistoryEntity(e.getClaimId(), timestamp);
    ClaimReportHistoryEntity updatedClaimHistoryEntry = ClaimReportHistoryEntity.copy(recentClaimHistoryEntry, timestamp);

    updatedClaimHistoryEntry.setCoveringEmployee(e.isCoveringEmployee());
    claimReportHistoryRepository.save(updatedClaimHistoryEntry);
  }

  private ClaimReportHistoryEntity getClaimReportHistoryEntity(String claimId, Instant timeOfKnowledge) {
    List<ClaimReportHistoryEntity> listOfClaims = claimReportHistoryRepository.findByClaimId(claimId);

    Optional<ClaimReportHistoryEntity> claimReportHistoryEntityMaybe = listOfClaims.stream()
      .filter(x -> !x.getTimeOfKnowledge().isAfter(timeOfKnowledge))
      .max(Comparator.comparing(ClaimReportHistoryEntity::getTimeOfKnowledge));

    if (!claimReportHistoryEntityMaybe.isPresent()) {
      log.error("Claim {} cannot be found in the claimReportHistoryRepository.", claimId);
      throw new RuntimeException("Claim cannot be found in the claimReportHistoryRepository");
    }

    return claimReportHistoryEntityMaybe.get();
  }
}
