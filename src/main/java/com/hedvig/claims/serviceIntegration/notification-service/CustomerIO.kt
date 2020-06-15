package com.hedvig.claims.serviceIntegration.`notification-service`

import java.time.Instant
import java.time.ZoneId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("customer.io")
@Component
@EnableFeignClients
class CustomerIO(
    private val notificationServiceClient: NotificationServiceClient
) {
    val logger: Logger = LoggerFactory.getLogger(CustomerIO::class.java)

    fun notifyClaimOpened(userId: String, claimId: String, timestamp: Instant) {
        try {
            this.customerIOClient.postUserEvent(
                userId = userId,
                event = CustomerIOEvent(
                    name = "claim_opened",
                    data = mapOf(
                        "claim_id" to claimId,
                        "opened_at" to timestamp.atZone(ZoneId.of("Europe/Stockholm")).toEpochSecond()
                    )
                )
            )
        } catch (exception: Exception) {
            logger.error("Could not notify claim for member=$userId opened to customer.io (exception=$exception)")
        }
    }

    fun notifyClaimClosed(userId: String, claimId: String, timestamp: Instant) {
        try {
            this.customerIOClient.postUserEvent(
                userId = userId,
                event = CustomerIOEvent(
                    name = "claim_closed",
                    data = mapOf(
                        "claim_id" to claimId,
                        "closed_at" to timestamp.atZone(ZoneId.of("Europe/Stockholm")).toEpochSecond()
                    )
                )
            )
        } catch (exception: Exception) {
            logger.error("Could not notify claim for member=$userId closed to customer.io (exception=$exception)")
        }
    }

    fun notifyClaimReopened(userId: String, claimId: String, timestamp: Instant) {
        try {
            this.customerIOClient.postUserEvent(
                userId = userId,
                event = CustomerIOEvent(
                    name = "claim_reopened",
                    data = mapOf(
                        "claim_id" to claimId,
                        "reopened_at" to timestamp.atZone(ZoneId.of("Europe/Stockholm")).toEpochSecond()
                    )
                )
            )
        } catch (exception: Exception) {
            logger.error("Could not notify claim for member=$userId reopened to customer.io (exception=$exception)")
        }
    }
}
