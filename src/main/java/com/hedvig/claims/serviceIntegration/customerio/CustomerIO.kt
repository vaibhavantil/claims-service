package com.hedvig.claims.serviceIntegration.customerio

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId

@Profile("customer.io")
@ConditionalOnProperty(value = ["customerio.siteId", "customerio.apiKey"], matchIfMissing = false)
@Component
@EnableFeignClients
class CustomerIO(
    private val customerIOClient: CustomerIOClient
) {
    val logger: Logger = LoggerFactory.getLogger(CustomerIO::class.java)

    fun notifyClaimClosed(userId: String, timestamp: Instant) {
        try {
            this.customerIOClient.postUserEvent(
                userId = userId,
                event = CustomerIOEvent(
                    name = "claim-closed",
                    data = mapOf(
                        "closed_at" to timestamp.atZone(ZoneId.of("Europe/Stockholm")).toEpochSecond()
                    )
                )
            )
        } catch (exception: Exception) {
            logger.error("Could not notify claim for member=$userId closed to customer.io (exception=$exception)")
        }
    }
}
