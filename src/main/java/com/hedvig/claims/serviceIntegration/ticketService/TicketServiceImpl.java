package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates.CLOSED;

@Slf4j
@Service
public class TicketServiceImpl implements com.hedvig.claims.serviceIntegration.ticketService.TicketService {

  private TicketServiceClient client;

  @Autowired
  public TicketServiceImpl(TicketServiceClient ticketServiceClient) {
    this.client = ticketServiceClient;
  }

  @Override
  public void closeClaimTicket(ClaimsAggregate.ClaimStates state, String userId, String claimId) {
      if (state.equals(CLOSED)) {
        client.closeClaimTicket(claimId);
      }
  }
}
