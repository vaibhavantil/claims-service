package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.events.BackofficeClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.services.ReportGenerationService;
import org.axonframework.eventhandling.ReplayStatus;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

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
  public void test() {
    Mockito.when(this.reportGenerationService.getReportPeriod()).thenReturn(YearMonth.now());

    reportEventListener.on(new BackofficeClaimCreatedEvent(CLAIM_ID, MEMBER_ID, Instant.now(), ClaimSource.INTERCOM), Instant.now(), ReplayStatus.REPLAY);

    ArgumentCaptor<ClaimReportEntity> argument = ArgumentCaptor.forClass(ClaimReportEntity.class);

    Mockito.verify(claimReportRepository, Mockito.times(1)).save(argument.capture());
    assertThat(argument.getValue().getClaimId()).isEqualTo(CLAIM_ID);
  }

}
