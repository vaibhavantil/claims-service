package com.hedvig.claims.serviceIntegration.ticketService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")

public interface TicketServiceClient {

  @PostMapping(value = "/_/tickets/claim/{claimId}/close")
  ResponseEntity<Void> closeClaimTicket(@PathVariable(name = "claimId") String claimId);
}
