package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.serviceIntegration.ticketService.dto.ClaimToTicketDto;


public interface TicketService {
  void createNewTicket (ClaimToTicketDto claimToTicket );
}
