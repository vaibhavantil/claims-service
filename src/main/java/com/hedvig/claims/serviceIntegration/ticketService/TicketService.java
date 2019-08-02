package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateTicketDto;


public interface TicketService {
  void createNewTicket (CreateTicketDto ticket );
}
