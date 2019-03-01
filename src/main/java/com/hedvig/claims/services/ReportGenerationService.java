package com.hedvig.claims.services;

import com.hedvig.claims.web.dto.ReportDTO;

import java.time.YearMonth;

public interface ReportGenerationService {

  ReportDTO generateReport(YearMonth yearMonth);

  YearMonth getReportPeriod();

  void replay(YearMonth yearMonth);
}
