//package com.hedvig.claims.query
//
//import com.hedvig.claims.events.ClaimCreatedEvent
//import com.hedvig.claims.services.ReportGenerationService
//import mu.KotlinLogging
//import org.axonframework.config.ProcessingGroup
//import org.axonframework.eventhandling.ReplayStatus
//import org.axonframework.eventhandling.Timestamp
//import org.springframework.context.event.EventListener
//import org.springframework.stereotype.Component
//import java.time.Instant
//import java.time.LocalDate
//import java.time.ZoneId
//import javax.transaction.Transactional
//
//@Component
//@Transactional
//@ProcessingGroup("report")
//class ReportEventListener {
//    private val logger = KotlinLogging.logger {}
//
//    lateinit var claimReportRepository: ClaimReportRepository
//    lateinit var reportGenerationService: ReportGenerationService
//
//    @EventListener
//    fun on(e: ClaimCreatedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
//        if (replayStatus.isReplay) {
//            logger.info { "Reporting year: ${reportGenerationService.getReportPeriod()}" }
//            logger.info { "ClaimCreated, Status : $replayStatus, timestamp: $timestamp, event: $e" }
//
//            claimReportRepository.save(
//                ClaimReportEntity(
//                    e.id,
//                    e.userId,
//                    LocalDate.ofInstant(timestamp, ZoneId.of("Europe/Stockholm")),
//                    "OPEN",
//                    LocalDate.ofInstant(timestamp, ZoneId.of("Europe/Stockholm"))
//                )
//            )
//        }
//    }
//}
