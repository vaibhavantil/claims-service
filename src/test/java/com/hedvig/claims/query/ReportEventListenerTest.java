package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.events.*;
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import com.hedvig.claims.services.ReportGenerationService;
import com.hedvig.claims.web.dto.ClaimDataType;
import org.axonframework.eventhandling.ReplayStatus;
import org.axonframework.eventsourcing.GenericDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.ConcatenatingDomainEventStream;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ReportEventListenerTest {

  @Mock
  private ClaimReportRepository claimReportRepository;
  @Mock
  private ReportGenerationService reportGenerationService;
  @Mock
  private EventStore eventStore;
  private ReportEventListener reportEventListener;

  private static String CLAIM_ID = "RemarkableClaimId";
  private static String MEMBER_ID = "RemarkableMemberId";
  private static String AUDIO_URL = "RemarkableAudioUrl";
  private static String AUTOMATIC_PAYMENT_ADDED_EVENT_ID = "RemarkableId";
  private static MonetaryAmount PAYOUT = Money.of(6895, "SEK");
  private static MonetaryAmount DEDUCTIBLE = Money.of(1500, "SEK");

  @Before
  public void onSetup() {
    MockitoAnnotations.initMocks(this);
    reportEventListener = new ReportEventListener(this.claimReportRepository, this.reportGenerationService, this.eventStore);
  }

  @Test
  public void WhenReplayClaimCreatedEvent_SaveToReportTable() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());

    reportEventListener.on(new ClaimCreatedEvent(CLAIM_ID, MEMBER_ID, AUDIO_URL), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
  }

  @Test
  public void WhenReplayBackOfficeClaimCreatedEvent_SaveToReportTable() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());

    reportEventListener.on(new BackofficeClaimCreatedEvent(CLAIM_ID, MEMBER_ID, Instant.now(), ClaimSource.INTERCOM), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
  }

  @Test
  public void WhenReplayClaimsReserveUpdateEvent_SaveReservedAndCurrency() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());
    Mockito.when(claimReportRepository.findById(any())).thenReturn(Optional.of(makeClaimReporEntity()));

    reportEventListener.on(new ClaimsReserveUpdateEvent(CLAIM_ID, LocalDateTime.now(), MEMBER_ID, PAYOUT.getNumber().doubleValueExact()), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
    assertThat(argument.getValue().getReserved()).isEqualTo(BigDecimal.valueOf(PAYOUT.getNumber().doubleValueExact()));
    assertThat(argument.getValue().getCurrency()).isEqualTo("SEK");
  }

  @Test
  public void WhenReplayDataItemAddedEvent_SaveDateOfLoss() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());
    Mockito.when(claimReportRepository.findById(any())).thenReturn(Optional.of(makeClaimReporEntity()));

    Instant dateOfLoss = Instant.now();
    LocalDate dateOfLossLocalDate = LocalDateTime.ofInstant(dateOfLoss, ZoneId.of("Europe/Stockholm")).toLocalDate();

    reportEventListener.on(new DataItemAddedEvent("1234", CLAIM_ID, LocalDateTime.now(), MEMBER_ID, ClaimDataType.DataType.DATE, "date", "date", true, dateOfLoss.toString()), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
    assertThat(argument.getValue().getDateOfLoss()).isEqualTo(dateOfLossLocalDate);
    assertThat(argument.getValue().getClaimYear()).isEqualTo(dateOfLossLocalDate.getYear());
  }

  @Test
  public void WhenReplayAutomaticPaymentInitiatedEvent_SaveToAmountFromAutomaticPaymentAddedEvent() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());
    Mockito.when(claimReportRepository.findById(any())).thenReturn(Optional.of(makeClaimReporEntity()));

    Mockito.when(eventStore.readEvents(any())).thenReturn(makeStream());

    reportEventListener.on(new AutomaticPaymentInitiatedEvent(AUTOMATIC_PAYMENT_ADDED_EVENT_ID, CLAIM_ID, MEMBER_ID, UUID.randomUUID(), TransactionStatus.COMPLETED), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
    assertThat(argument.getValue().getGrossPaid()).isEqualTo(BigDecimal.valueOf(PAYOUT.getNumber().doubleValueExact()));
    assertThat(argument.getValue().getCurrency()).isEqualTo("SEK");
  }

  private ClaimReportEntity makeClaimReporEntity() {
    return new ClaimReportEntity(CLAIM_ID, MEMBER_ID, LocalDate.now(), LocalDate.now(),"OPEN", LocalDate.now(), false);
  }

  private DomainEventStream makeStream() {
    GenericDomainEventMessage<AutomaticPaymentAddedEvent> event = new GenericDomainEventMessage<>(AutomaticPaymentAddedEvent.class.getTypeName(), CLAIM_ID, 0, makeAutomaticPaymentAddedEvent(AUTOMATIC_PAYMENT_ADDED_EVENT_ID, CLAIM_ID, MEMBER_ID));

    return new ConcatenatingDomainEventStream(
      DomainEventStream.of(event)
    );
  }

  private AutomaticPaymentAddedEvent makeAutomaticPaymentAddedEvent(String id, String claimId, String memberId) {
    return new AutomaticPaymentAddedEvent(
      id,
      claimId,
      memberId,
      PAYOUT,
      DEDUCTIBLE,
      "Note123",
      false,
      "Handler",
      true);
  }

}
