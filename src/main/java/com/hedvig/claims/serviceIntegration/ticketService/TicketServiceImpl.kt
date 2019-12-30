package com.hedvig.claims.serviceIntegration.ticketService

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class TicketServiceImpl @Autowired constructor(
    private val client: TicketServiceClient
) : TicketService {
    override fun closeClaimTicket(userId: String?, claimId: String?) {
        client.closeClaimTicket(claimId)
    }
}
