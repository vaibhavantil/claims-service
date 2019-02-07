package com.hedvig.claims.services

import com.hedvig.claims.query.ClaimReportRepository
import com.hedvig.claims.web.dto.ReportDTO
import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.beans.factory.annotation.Autowired
import java.time.YearMonth

class ReportGenerationServiceImpl : ReportGenerationService {

    lateinit var reportingPeriod: YearMonth
    lateinit var reportRepository: ClaimReportRepository

    override fun getReportPeriod(): YearMonth {
        return reportingPeriod
    }

    @Autowired
    lateinit var eventProcessingConfiguration: EventProcessingConfiguration


    override fun generateReport(yearMonth: YearMonth): ReportDTO?{
        reportingPeriod = yearMonth
        eventProcessingConfiguration
            .eventProcessorByProcessingGroup(PROCESSOR_GROUP, TrackingEventProcessor::class.java)
            .ifPresent { trackingEventProcessor ->
                trackingEventProcessor.shutDown()
                trackingEventProcessor.resetTokens()
                trackingEventProcessor.start()
            }
        return null
    }

    companion object {
        const val PROCESSOR_GROUP: String = "report"
    }

}
