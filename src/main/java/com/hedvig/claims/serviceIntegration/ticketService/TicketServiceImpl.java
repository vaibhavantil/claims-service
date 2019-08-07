package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.serviceIntegration.ticketService.dto.ClaimToTicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
public class TicketServiceImpl implements com.hedvig.claims.serviceIntegration.ticketService.TicketService {

  private TicketServiceClient client;

  @Autowired
  public TicketServiceImpl(TicketServiceClient c) {
    this.client = c;
  }

  @Override
  public void createNewTicket(ClaimToTicketDto claimToTicket) {
    try {
      ResponseEntity response = client.createNewTicket(claimToTicket);

    } catch (RestClientResponseException e) {
      log.info("Error when posting a 'Create New Ticket' request to ticket-service:" + e);
    }
  }

  @Override
  public void updateClaimTicket(ClaimsAggregate.ClaimStates status, String userId, String claimId) {
    switch  (status){
      case CLOSED: {
        client.closeClaim(claimId, userId);
      } break;
      case REOPENED:{
        client.reopenClaim(claimId, userId);
      } break;
      default: {
        log.info("Got unexpected claim status, we do not handle: {status} yet!");
      } break;
    }
  }
}
