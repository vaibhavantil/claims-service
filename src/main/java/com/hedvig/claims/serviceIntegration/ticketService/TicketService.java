package com.hedvig.claims.serviceIntegration.ticketService;
import com.hedvig.claims.aggregates.ClaimsAggregate;

public interface TicketService {

  void closeClaimTicket(ClaimsAggregate.ClaimStates state, String userId, String claimId);
}
