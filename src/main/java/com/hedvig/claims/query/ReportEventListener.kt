package com.hedvig.claims.query

import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.claims.services.ReportGenerationService
import mu.KotlinLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ReplayStatus
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.*
import javax.transaction.Transactional

@Component
@Transactional
@ProcessingGroup("report")
class ReportEventListener {

    @Autowired
    lateinit var claimReportRepository: ClaimReportRepository

    @Autowired
    lateinit var reportGenerationService: ReportGenerationService

    @EventHandler
    fun on(e: ClaimCreatedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            claimReportRepository.save(
                ClaimReportEntity(
                    e.id,
                    e.userId,
                    LocalDate.ofInstant(timestamp, ZoneId.of("Europe/Stockholm")),
                    "OPEN",
                    LocalDate.ofInstant(timestamp, ZoneId.of("Europe/Stockholm"))
                )
            )
        }
    }

    @ResetHandler
    fun onReset() {
        logger.info { "Deleting Database" }
        claimReportRepository.deleteAll()
    }

    private fun isBeforePeriod(yearMonth: YearMonth, timestamp: Instant): Boolean {
        return timestamp.isBefore(
            yearMonth.atEndOfMonth()
                .atTime(LocalTime.MAX).toInstant(ZoneId.of("Europe/Stockholm").rules.getOffset(Instant.now()))
        )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
