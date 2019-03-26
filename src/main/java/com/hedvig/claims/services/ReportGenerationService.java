package com.hedvig.claims.services;

import com.hedvig.claims.web.dto.MiReportClaimHistoryDTO;
import com.hedvig.claims.web.dto.ReportDTO;

import java.time.YearMonth;
import java.util.List;

public interface ReportGenerationService {

  ReportDTO generateReport(YearMonth yearMonth);

  YearMonth getReportPeriod();

  void replay(YearMonth yearMonth);

  List<MiReportClaimHistoryDTO> generateMiReport(YearMonth until);
}
