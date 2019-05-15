package com.hedvig.claims.services;

import com.hedvig.claims.query.ClaimReportHistoryEntity;
import com.hedvig.claims.query.ClaimReportHistoryRepository;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.web.dto.ClaimReportDTO;
import com.hedvig.claims.web.dto.ReportClaimHistoryDTO;
import com.hedvig.claims.web.dto.ReportDTO;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {

  private static final String EUROPE_STOCKHOLM = "Europe/Stockholm";
  public static final String TEST = "Test";
  public static final String NOT_COVERED = "Not covered";
  private static String REPORTING_PROCESSOR_GROUP = "liveReporting";

  private EventProcessingConfiguration eventProcessingConfiguration;
  private ClaimReportHistoryRepository claimReportHistoryRepository;
  private ClaimsRepository claimsRepository;

  @Autowired
  public ReportGenerationServiceImpl(EventProcessingConfiguration eventProcessingConfiguration,
                                     ClaimReportHistoryRepository claimReportHistoryRepository,
                                     ClaimsRepository claimsRepository) {
    this.eventProcessingConfiguration = eventProcessingConfiguration;
    this.claimReportHistoryRepository = claimReportHistoryRepository;
    this.claimsRepository = claimsRepository;
  }

  public ReportDTO generateReport(YearMonth yearMonth) {
    final List<String> testClaimIds = fetchCurrentTestClaims();
    final Map<String, List<ClaimReportHistoryEntity>> claimHistoryEntities = claimReportHistoryRepository.findAll().stream()
      .filter(historyEntity -> !testClaimIds.contains(historyEntity.getClaimId()))
      .filter(claimReportHistoryEntity -> !claimReportHistoryEntity.getTimeOfKnowledge()
        .isAfter(
          yearMonth
            .atEndOfMonth()
            .atTime(23, 59, 59, 999_999_999)
            .atZone(ZoneId.of(EUROPE_STOCKHOLM))
            .toInstant()
        )
      )
      .collect(Collectors.groupingBy(ClaimReportHistoryEntity::getClaimId));

    final Map<String, ClaimReportHistoryEntity> latestClaimByIds = claimHistoryEntities.entrySet().stream()
      .map(
        entrySet -> new AbstractMap.SimpleEntry<>(
          entrySet.getKey(),
          entrySet.getValue().stream()
            .max(Comparator.comparing(ClaimReportHistoryEntity::getTimeOfKnowledge)).get()
        )
      )
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


    final Stream<ClaimReportDTO> reportStream = latestClaimByIds.values().stream()
      .map(ClaimReportDTO::fromClaimReportHistoryEntity);

    return new ReportDTO(reportStream);
  }

  public void replay() {
    eventProcessingConfiguration
      .eventProcessorByProcessingGroup(REPORTING_PROCESSOR_GROUP, TrackingEventProcessor.class)
      .ifPresent(trackingEventProcessor -> {
        trackingEventProcessor.shutDown();
        trackingEventProcessor.resetTokens();
        trackingEventProcessor.start();
      });
  }

  public List<ReportClaimHistoryDTO> generateClaimsReport(YearMonth until) {

    List<String> testClaims = fetchCurrentTestClaims();

    return this.claimReportHistoryRepository.findAll().stream()
      .filter(historyEntity -> !testClaims.contains(historyEntity.getClaimId()))
      .filter(claimReportHistoryEntity -> !claimReportHistoryEntity.getTimeOfKnowledge()
        .isAfter(
          until
            .atEndOfMonth()
            .atTime(23, 59, 59, 999_999_999)
            .atZone(ZoneId.of(EUROPE_STOCKHOLM))
            .toInstant()
        )
      )
      .map(ReportClaimHistoryDTO::from)
      .collect(Collectors.toList());
  }

  private List<String> fetchCurrentTestClaims() {
    return claimsRepository.findByType(TEST)
      .stream()
      .map(claimEntity -> claimEntity.id)
      .collect(Collectors.toList());
  }
}
