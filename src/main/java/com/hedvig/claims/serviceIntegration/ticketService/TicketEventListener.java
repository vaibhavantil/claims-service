package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("TicketService")
public class TicketEventListener {

  private final TicketService ticketService;

  @Autowired
  public TicketEventListener(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @EventHandler
  public void on(ClaimStatusUpdatedEvent event) {
    if (event.state == ClaimsAggregate.ClaimStates.CLOSED) {
      ticketService.closeClaimTicket(event.getState(), event.getUserId(), event.getClaimsId());
    }
  }

}
