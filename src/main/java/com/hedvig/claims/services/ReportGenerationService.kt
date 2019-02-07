package com.hedvig.claims.services

import com.hedvig.claims.web.dto.ReportDTO
import java.time.YearMonth


interface ReportGenerationService {
    fun generateReport(yearMonth: YearMonth): ReportDTO?
    fun getReportPeriod(): YearMonth
}
