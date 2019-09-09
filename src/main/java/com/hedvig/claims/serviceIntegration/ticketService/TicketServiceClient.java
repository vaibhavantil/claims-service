package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateClaimTicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")

public interface TicketServiceClient {

  @PostMapping(value = "/_/tickets/claim")
  ResponseEntity<Void> createClaimTicket(@RequestBody CreateClaimTicketDto createClaimTicketRequest);

  @PostMapping(value = "/_/tickets/claim/{claimId}/close")
  ResponseEntity<Void> closeClaimTicket(@PathVariable(name = "claimId") String claimId);

  @PostMapping(value = "/_/tickets/claim/{claimId}/reopen")
  ResponseEntity<Void> reopenClaimTicket(@PathVariable(name = "claimId") String claimId);


}
