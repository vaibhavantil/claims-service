package com.hedvig.claims.services;

import com.hedvig.claims.web.dto.BDXReportClaimHistoryDTO;
import com.hedvig.claims.web.dto.ReportClaimHistoryDTO;
import com.hedvig.claims.web.dto.ReportDTO;

import java.time.YearMonth;
import java.util.List;

public interface ReportGenerationService {

  ReportDTO generateReport(YearMonth yearMonth);

  YearMonth getReportPeriod();

  void replay(YearMonth yearMonth);

  List<ReportClaimHistoryDTO> generateClaimsReport(YearMonth until);
}
