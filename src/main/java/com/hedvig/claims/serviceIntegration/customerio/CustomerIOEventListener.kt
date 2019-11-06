package com.hedvig.claims.serviceIntegration.customerio

import com.hedvig.claims.events.ClaimStatusUpdatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("customer.io")
@ConditionalOnProperty(value = ["customerio.siteId", "customerio.apiKey"], matchIfMissing = false)
@ProcessingGroup("CustomerIO")
@Component
class CustomerIOEventListener(
    private val customerIO: CustomerIO
) {
    @EventHandler
    fun on(event: ClaimStatusUpdatedEvent) {
        customerIO.setClaimStatus(event.userId, event.state)
    }
}
