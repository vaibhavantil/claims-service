package com.hedvig.claims.services;

import com.hedvig.claims.web.dto.ReportDTO;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {

  private YearMonth reportingPeriod;
  private static String REPORTING_PROCESSOR_GROUP = "report";

  @Autowired
  private EventProcessingConfiguration eventProcessingConfiguration;

  public YearMonth getReportPeriod() {
    return this.reportingPeriod;
  }

  public ReportDTO generateReport(YearMonth yearMonth) {
    this.reportingPeriod = yearMonth;

    eventProcessingConfiguration
      .eventProcessorByProcessingGroup(REPORTING_PROCESSOR_GROUP, TrackingEventProcessor.class)
      .ifPresent(trackingEventProcessor -> {
        trackingEventProcessor.shutDown();
        trackingEventProcessor.resetTokens();
        trackingEventProcessor.start();
      });

    return null;
  }
}
