package com.hedvig.claims.serviceIntegration.ticketService

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(value = "ticket-service", url = "\${hedvig.ticket-service.url:ticket-service}")
interface TicketServiceClient {
    @PostMapping(value = ["/_/tickets/claim/{claimId}/close"])
    fun closeClaimTicket(@PathVariable(name = "claimId") claimId: String?): ResponseEntity<Void?>?
}
