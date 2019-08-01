package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
public class TicketServiceImpl implements com.hedvig.claims.serviceIntegration.ticketService.TicketService {

  private TicketServiceClient ticketServiceclient;

  @Autowired
  public TicketServiceImpl ( TicketServiceClient c ) {
    this.ticketServiceclient = c;
  }

  @Override
  public void createNewTicket (TicketDto ticket ) {
    try {
      ResponseEntity response = ticketServiceclient.createNewTicket( ticket ) ;

    } catch (RestClientResponseException e ){
        log.info("Error when posting a 'Create New Ticket' request to ticket-service:" + e);
      }
    }
}
