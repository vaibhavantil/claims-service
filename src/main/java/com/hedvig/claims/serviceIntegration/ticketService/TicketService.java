package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.serviceIntegration.ticketService.dto.ClaimToTicketDto;


public interface TicketService {
  void createNewTicket (ClaimToTicketDto claimToTicket );

  void updateClaimTicket(ClaimsAggregate.ClaimStates status, String userId, String claimId);

}
