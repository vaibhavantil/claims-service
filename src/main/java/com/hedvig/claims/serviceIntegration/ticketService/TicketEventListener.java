package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.serviceIntegration.ticketService.dto.ClaimToTicketDto;
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
  public TicketEventListener(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @EventHandler
  public void on(ClaimCreatedEvent event, @Timestamp Instant timestamp) {
    StringBuilder sb = new StringBuilder();
    sb.append("A new claim created!\n");
    sb.append(event.getId());
    sb.append("\nCreated at: \n");
    sb.append(timestamp);

    String description = sb.toString();

    ClaimToTicketDto claimToTicket = new ClaimToTicketDto(
      event.getId(),
      event.getUserId(),
      description
    );

    ticketService.createNewTicket(claimToTicket);
  }

  @EventHandler
  public void on(ClaimStatusUpdatedEvent event) {
    ticketService.updateClaimTicket(event.getState(), event.getUserId(), event.getClaimsId());
  }

}
