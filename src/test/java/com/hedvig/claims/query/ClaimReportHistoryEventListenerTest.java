package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.events.BackofficeClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ClaimReportHistoryEventListenerTest {
  private final Instant NOW = Instant.parse("2019-05-09T09:17:14.0Z");
  private final Instant A_DAY = Instant.parse("2019-05-09T00:00:00.0Z");

  private ClaimReportHistoryRepository repository;
  private EventStore eventStore;
  private ClaimReportHistoryEventListener listener;

  @Before
  public void setUp() {
    repository = mock(ClaimReportHistoryRepository.class);
    eventStore = mock(EventStore.class);
    listener = new ClaimReportHistoryEventListener(repository, eventStore);
  }

  @Test
  public void createsClaim() {
    final ClaimCreatedEvent e = new ClaimCreatedEvent(randomUUID().toString(), "123", "s3://blargh");

    listener.on(e, NOW);

    final ArgumentCaptor<ClaimReportHistoryEntity> saveCaptor = ArgumentCaptor.forClass(ClaimReportHistoryEntity.class);
    verify(repository).save(saveCaptor.capture());
    final ClaimReportHistoryEntity savedResult = saveCaptor.getValue();

    assertThat(savedResult.getClaimId()).isEqualTo(e.getId());
    assertThat(savedResult.getTimeOfKnowledge()).isEqualTo(NOW);
  }

  @Test
  public void createsBackOfficeClaim() {
    final BackofficeClaimCreatedEvent e = new BackofficeClaimCreatedEvent(
      randomUUID().toString(),
      "123",
      A_DAY,
      ClaimSource.INTERCOM
    );

    listener.on(e, NOW);

    final ArgumentCaptor<ClaimReportHistoryEntity> saveCaptor = ArgumentCaptor.forClass(ClaimReportHistoryEntity.class);
    verify(repository).save(saveCaptor.capture());
    final ClaimReportHistoryEntity savedResult = saveCaptor.getValue();

    assertThat(savedResult.getClaimId()).isEqualTo(e.getId());
    assertThat(savedResult.getTimeOfKnowledge()).isEqualTo(NOW);
  }

  @Test
  public void updatesClaimStatus() {
    final ClaimStatusUpdatedEvent e = new ClaimStatusUpdatedEvent(
      randomUUID().toString(),
      "123",
      A_DAY.atZone(ZoneId.of("UTC")).toLocalDateTime(),
      ClaimsAggregate.ClaimStates.CLOSED
    );

    final ClaimReportHistoryEntity existingEntity = new ClaimReportHistoryEntity(
      e.getClaimsId(),
      e.getUserId(),
      NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
      NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
      ClaimsAggregate.ClaimStates.OPEN.toString(),
      false,
      NOW.minusSeconds(3600 * 24)
    );
    existingEntity.setReserved(BigDecimal.TEN);
    final List<ClaimReportHistoryEntity> existingEntities = List.of(existingEntity);
    when(repository.findByClaimId(e.getClaimsId())).thenReturn(existingEntities);

    listener.on(e, NOW);

    final ArgumentCaptor<ClaimReportHistoryEntity> saveCaptor = ArgumentCaptor.forClass(ClaimReportHistoryEntity.class);
    verify(repository).save(saveCaptor.capture());
    final ClaimReportHistoryEntity savedResult = saveCaptor.getValue();

    assertThat(savedResult.getClaimId()).isEqualTo(e.getClaimsId());
    assertThat(savedResult.getTimeOfKnowledge()).isEqualTo(NOW);
    assertThat(savedResult.getClaimStatus()).isEqualTo(ClaimsAggregate.ClaimStates.CLOSED.toString());
    assertThat(savedResult.getReserved()).isEqualTo(BigDecimal.TEN);
  }

  @Test
  public void updatesReserves() {
    final ClaimsReserveUpdateEvent e = new ClaimsReserveUpdateEvent(
      randomUUID().toString(),
      A_DAY.atZone(ZoneId.of("UTC")).toLocalDateTime(),
      "123",
      42d
    );

    final List<ClaimReportHistoryEntity> existingEntities = List.of(
      new ClaimReportHistoryEntity(
        e.getClaimID(),
        e.getUserId(),
        NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
        NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
        ClaimsAggregate.ClaimStates.OPEN.toString(),
        false,
        NOW.minusSeconds(3600 * 24)
      )
    );
    when(repository.findByClaimId(e.getClaimID())).thenReturn(existingEntities);

    listener.on(e, NOW);

    final ArgumentCaptor<ClaimReportHistoryEntity> saveCaptor = ArgumentCaptor.forClass(ClaimReportHistoryEntity.class);
    verify(repository).save(saveCaptor.capture());
    final ClaimReportHistoryEntity savedResult = saveCaptor.getValue();

    assertThat(savedResult.getClaimId()).isEqualTo(e.getClaimID());
    assertThat(savedResult.getTimeOfKnowledge()).isEqualTo(NOW);
    assertThat(savedResult.getReserved()).isEqualTo(BigDecimal.valueOf(42d));
  }

  @Test
  public void doesNotCopyReservesWhenClaimIsClosed() {
    final ClaimsReserveUpdateEvent e = new ClaimsReserveUpdateEvent(
      randomUUID().toString(),
      A_DAY.atZone(ZoneId.of("UTC")).toLocalDateTime(),
      "123",
      42d
    );

    final List<ClaimReportHistoryEntity> existingEntities = List.of(
      new ClaimReportHistoryEntity(
        e.getClaimID(),
        e.getUserId(),
        NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
        NOW.atZone(ZoneId.of("UTC")).toLocalDate().minusDays(1),
        ClaimsAggregate.ClaimStates.CLOSED.toString(),
        false,
        NOW.minusSeconds(3600 * 24)
      )
    );
    when(repository.findByClaimId(e.getClaimID())).thenReturn(existingEntities);

    listener.on(e, NOW);

    final ArgumentCaptor<ClaimReportHistoryEntity> saveCaptor = ArgumentCaptor.forClass(ClaimReportHistoryEntity.class);
    verify(repository).save(saveCaptor.capture());
    final ClaimReportHistoryEntity savedResult = saveCaptor.getValue();

    assertThat(savedResult.getClaimId()).isEqualTo(e.getClaimID());
    assertThat(savedResult.getTimeOfKnowledge()).isEqualTo(NOW);
    assertThat(savedResult.getReserved()).isEqualTo(BigDecimal.valueOf(42d));
  }
}
