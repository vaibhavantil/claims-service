package com.hedvig.claims.serviceIntegration.ticketService

interface TicketService {
  fun closeClaimTicket(userId: String?, claimId: String?)
}
