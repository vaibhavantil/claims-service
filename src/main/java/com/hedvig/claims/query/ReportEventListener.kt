package com.hedvig.claims.query

import com.hedvig.claims.events.*
import com.hedvig.claims.services.ReportGenerationService
import mu.KotlinLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ReplayStatus
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.*
import java.util.*
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

    @EventHandler
    fun on(e: BackofficeClaimCreatedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            claimReportRepository.save(
                ClaimReportEntity(
                    e.id,
                    e.memberId,
                    LocalDate.ofInstant(e.registrationDate, ZoneId.of("Europe/Stockholm")),
                    "OPEN",
                    LocalDate.ofInstant(e.registrationDate, ZoneId.of("Europe/Stockholm"))
                )
            )
        }
    }

    @EventHandler
    fun on(e: ClaimStatusUpdatedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimsId)

            claim.claimStatus = e.state.toString()
            claim.claimStatusLastUpdated =
                LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Stockholm")).toLocalDate()

            claimReportRepository.save(claim)
        }
    }

    @EventHandler
    fun on(e: ClaimsTypeUpdateEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimID)

            claim.descriptionOfLoss = e.type
            claimReportRepository.save(claim)
        }
    }

    @EventHandler
    fun on(e: ClaimsReserveUpdateEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimID)

            claim.reserved = BigDecimal.valueOf(e.amount)
            claimReportRepository.save(claim)
        }
    }

    @EventHandler
    fun on(e: DataItemAddedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimsId)

            if (e.name == DATE) {
                claim.dateOfLoss =
                    LocalDateTime.ofInstant(Instant.parse(e.value), ZoneId.of("Europe/Stockholm")).toLocalDate()
            }
        }
    }

    @EventHandler
    fun on(e: PaymentAddedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimsId)
            claim.grossPaid = claim.grossPaid ?: BigDecimal.ZERO + BigDecimal.valueOf(e.amount)
            claimReportRepository.save(claim)
        }
    }

    @EventHandler
    fun on(e: AutomaticPaymentAddedEvent, @Timestamp timestamp: Instant, replayStatus: ReplayStatus) {
        if (replayStatus.isReplay && isBeforePeriod(reportGenerationService.getReportPeriod(), timestamp)) {
            val claim: ClaimReportEntity = getClaimReportEntity(e.claimId)
            claim.grossPaid =
                claim.grossPaid ?: BigDecimal.ZERO + BigDecimal.valueOf(e.amount.number.doubleValueExact())
            claimReportRepository.save(claim)
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

    private fun getClaimReportEntity(claimId: String): ClaimReportEntity {
        val optionalClaim: Optional<ClaimReportEntity> = claimReportRepository.findById(claimId)
        if (!optionalClaim.isPresent) {
            logger.error { "Claim $claimId cannot be found in the claimReportRepository." }
            throw RuntimeException("Claim $claimId cannot be found in the claimReportRepository")
        }
        return optionalClaim.get()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        const val DATE: String = "DATE"
    }
}
