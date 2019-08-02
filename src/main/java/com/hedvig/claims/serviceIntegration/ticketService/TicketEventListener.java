package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateTicketDto;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@ProcessingGroup("TicketService")
public class TicketEventListener {

  private final TicketService ticketService;

  @Autowired
  public TicketEventListener ( TicketService ticketService ) {
    this.ticketService = ticketService;
  }

  @EventHandler
  public void on(ClaimCreatedEvent event, @Timestamp Instant timestamp) {
    StringBuilder sb = new StringBuilder();
    sb.append("A new claim with id: \n");
    sb.append(event.getId());
    sb.append("\nfrom user with id: \n");
    sb.append(event.getUserId());
    sb.append("\nClaim audio url: \n");
    sb.append(event.getAudioURL());
    sb.append("\nCreated at: \n");
    sb.append(timestamp);

    String description = sb.toString();

    CreateTicketDto ticket = new CreateTicketDto(
      event.getUserId(),
      "claims-service",
      "Unassigned",
      event.getId(),
      null, //The priority is automatically handled in Ticket-Service
      TicketType.CLAIM,
      null,
      null,
      "",
      description,
      TicketStatus.WAITING
    ) ;
    ticketService.createNewTicket(ticket);
  }
}


