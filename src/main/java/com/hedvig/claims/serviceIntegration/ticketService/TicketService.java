package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateClaimTicketDto;


public interface TicketService {
  void createClaimTicket(CreateClaimTicketDto claimToTicket );

  void updateClaimTicketState(ClaimsAggregate.ClaimStates state, String userId, String claimId);
}
