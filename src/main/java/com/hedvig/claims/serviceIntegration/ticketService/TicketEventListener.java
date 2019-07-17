package com.hedvig.claims.serviceIntegration.ticketService;


import com.hedvig.claims.events.BackofficeClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@ProcessingGroup("Tickets")
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

    TicketDto ticket = new TicketDto (
      event.getUserId(),
      "claims-service@hedvig.com",
      "Unassigned",
      0.5f,
      TicketType.CLAIM,
      null,
      null,
      "",
      description,
      TicketStatus.WAITING
    ) ;
    ticketService.createNewTicket(event.getId(), ticket );
  }


  @EventHandler
  public void on(BackofficeClaimCreatedEvent event, @Timestamp Instant timestamp ){
    StringBuilder sb = new StringBuilder();
    sb.append("A new claim with id: \n");
    sb.append(event.getId());
    sb.append("\nfrom user with id: \n");
    sb.append(event.getMemberId());
    sb.append("\nCreated at: \n");
    sb.append(timestamp);

    String description = sb.toString();

    TicketDto ticket = new TicketDto(
      event.getMemberId(),
      "claims-service@hedvig.com",
      "Unassigned",
      0.66f,
      TicketType.CLAIM,
      null,
      null,
      "",
      description,
      TicketStatus.WAITING
    );

    ticketService.createNewTicket(event.getId(), ticket );
  }




}


