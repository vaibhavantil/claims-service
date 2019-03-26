package com.hedvig.claims.services;

import com.hedvig.claims.query.ClaimReportHistoryRepository;
import com.hedvig.claims.query.ClaimReportRepository;
import com.hedvig.claims.web.dto.ClaimReportDTO;
import com.hedvig.claims.web.dto.MiReportClaimHistoryDTO;
import com.hedvig.claims.web.dto.ReportDTO;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {

  private YearMonth reportingPeriod;
  private static String REPORTING_PROCESSOR_GROUP = "report";

  private ClaimReportRepository claimReportRepository;
  private EventProcessingConfiguration eventProcessingConfiguration;
  private ClaimReportHistoryRepository claimReportHistoryRepository;

  @Autowired
  public ReportGenerationServiceImpl(EventProcessingConfiguration eventProcessingConfiguration,
                                     ClaimReportRepository claimReportRepository,
                                     ClaimReportHistoryRepository claimReportHistoryRepository) {
    this.eventProcessingConfiguration = eventProcessingConfiguration;
    this.claimReportRepository = claimReportRepository;
    this.claimReportHistoryRepository = claimReportHistoryRepository;
  }

  public YearMonth getReportPeriod() {
    return this.reportingPeriod;
  }

  public ReportDTO generateReport(YearMonth yearMonth) {
    this.reportingPeriod = yearMonth;

    Stream<ClaimReportDTO> reportStream = claimReportRepository.findAll().stream().map(ClaimReportDTO::fromClaimReportEntity);

    return new ReportDTO(reportStream);
  }

  public void replay(YearMonth yearMonth) {
    this.reportingPeriod = yearMonth;

    eventProcessingConfiguration
      .eventProcessorByProcessingGroup(REPORTING_PROCESSOR_GROUP, TrackingEventProcessor.class)
      .ifPresent(trackingEventProcessor -> {
        trackingEventProcessor.shutDown();
        trackingEventProcessor.resetTokens();
        trackingEventProcessor.start();
      });
  }

  public List<MiReportClaimHistoryDTO> generateMiReport(YearMonth until) {
    return this.claimReportHistoryRepository.findAll().stream()
      .filter(h -> !h.getTimeOfKnowledge()
        .isAfter(
          until
            .atEndOfMonth()
            .atTime(23,59,59,999_999_999)
            .atZone(ZoneId.of("Europe/Stockholm"))
            .toInstant()
        )
      )
      .map(MiReportClaimHistoryDTO::from)
      .collect(Collectors.toList());
  }
}
