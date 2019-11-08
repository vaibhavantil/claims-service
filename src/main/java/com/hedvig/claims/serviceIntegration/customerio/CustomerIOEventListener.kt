package com.hedvig.claims.serviceIntegration.customerio

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import com.hedvig.claims.events.BackofficeClaimCreatedEvent
import com.hedvig.claims.events.ClaimCreatedEvent
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
    fun on(event: ClaimCreatedEvent, @Timestamp timestamp: Instant) {
        customerIO.notifyClaimOpened(
            userId = event.userId,
            claimId = event.id,
            timestamp = timestamp
        )
    }

    @EventHandler
    fun on(event: BackofficeClaimCreatedEvent, @Timestamp timestamp: Instant) {
        customerIO.notifyClaimOpened(
            userId = event.memberId,
            claimId = event.id,
            timestamp = timestamp
        )
    }

    @EventHandler
    fun on(event: ClaimStatusUpdatedEvent, @Timestamp timestamp: Instant) {
        when (event.state!!) {
            ClaimStates.OPEN -> customerIO.notifyClaimOpened(
                userId = event.userId,
                claimId = event.claimsId,
                timestamp = timestamp
            )
            ClaimStates.CLOSED -> customerIO.notifyClaimClosed(
                userId = event.userId,
                claimId = event.claimsId,
                timestamp = timestamp
            )
            ClaimStates.REOPENED -> customerIO.notifyClaimReopened(
                userId = event.userId,
                claimId = event.claimsId,
                timestamp = timestamp
            )
        }
    }
}
