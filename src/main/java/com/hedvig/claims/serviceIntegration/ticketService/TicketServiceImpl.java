package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateClaimTicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketServiceImpl implements com.hedvig.claims.serviceIntegration.ticketService.TicketService {

  private TicketServiceClient client;

  @Autowired
  public TicketServiceImpl(TicketServiceClient ticketServiceClient) {
    this.client = ticketServiceClient;
  }

  @Override
  public void createClaimTicket(CreateClaimTicketDto createClaimTicketRequest) {
    try {
      client.createClaimTicket(createClaimTicketRequest);
    } catch (Exception exception) {
      log.info("Error when posting a 'Create New Ticket' request to ticket-service:" + exception.getMessage());
    }
  }

  @Override
  public void updateClaimTicketState(ClaimsAggregate.ClaimStates state, String userId, String claimId) {
    switch  (state){
      case CLOSED: {
        client.closeClaimTicket(claimId);
      } break;
      case REOPENED:
      case OPEN:
        client.reopenClaimTicket(claimId);
        break;
      default:
        throw new IllegalArgumentException("Got unexpected claim status, we do not handle this status: " + state.toString());
    }
  }
}
