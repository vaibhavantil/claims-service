package com.hedvig.claims.query

import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.claims.services.ReportGenerationService
import mu.KotlinLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ReplayStatus
import org.axonframework.eventhandling.Timestamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
        if (replayStatus.isReplay) {
            logger.info { "Reporting year: ${reportGenerationService.getReportPeriod()}" }
            logger.info { "ClaimCreated,, timestamp: $timestamp, event: $e" }

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

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
