package com.hedvig.claims.serviceIntegration.customerio

import com.hedvig.claims.aggregates.ClaimsAggregate
import com.hedvig.claims.events.ClaimStatusUpdatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant

@Profile("customer.io")
@ConditionalOnProperty(value = ["customerio.siteId", "customerio.apiKey"], matchIfMissing = false)
@ProcessingGroup("CustomerIO")
@Component
class CustomerIOEventListener(
    private val customerIO: CustomerIO
) {
    @EventHandler
    fun on(event: ClaimStatusUpdatedEvent, @Timestamp timestamp: Instant) {
        if (event.state == ClaimsAggregate.ClaimStates.CLOSED) {
            customerIO.notifyClaimClosed(event.userId, timestamp)
        }
    }
}
