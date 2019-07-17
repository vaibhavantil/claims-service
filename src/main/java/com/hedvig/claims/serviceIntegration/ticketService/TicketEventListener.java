package com.hedvig.claims.serviceIntegration.ticketService;


import com.hedvig.claims.aggregates.ClaimSource;
import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

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
    sb.append("A new claim with id: ");
    sb.append(event.getId());
    sb.append(" from user with id: ");
    sb.append(event.getUserId());
    sb.append("\nClaim audio url: ");
    sb.append(event.getAudioURL());
    sb.append("\nCreated at: ");
    sb.append(timestamp);

    String description = sb.toString();

    TicketDto ticket = new TicketDto (
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
}


