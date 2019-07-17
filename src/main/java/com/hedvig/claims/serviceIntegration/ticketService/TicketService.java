package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;


public interface TicketService {

  void createNewTicket (String claimId, TicketDto claim );


}
