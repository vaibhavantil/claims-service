package com.hedvig.claims.serviceIntegration.ticketService

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import com.hedvig.claims.events.ClaimStatusUpdatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("TicketService")
class TicketEventListener @Autowired constructor(
    private val ticketService: TicketService
) {
    @EventHandler
    fun on(event: ClaimStatusUpdatedEvent) {
        if (event.state == ClaimStates.CLOSED) {
            ticketService.closeClaimTicket(event.userId, event.claimsId)
        }
    }
}
