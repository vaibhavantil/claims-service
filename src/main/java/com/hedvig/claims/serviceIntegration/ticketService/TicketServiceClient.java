package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.ClaimToTicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")

public interface TicketServiceClient {

  @PostMapping(value = "/_/tickets/claim")
  ResponseEntity<Void> createNewTicket(@RequestBody ClaimToTicketDto claimToTicket);

  @PostMapping(value = "/_/tickets/claim/{claimId}/close")
  ResponseEntity<Void> closeClaim(@PathVariable(name = "claimId") String claimId, @RequestBody String memberId);

  @PostMapping(value = "/_/tickets/claim/{claimId}/reopen")
  ResponseEntity<Void> reopenClaim(@PathVariable(name = "claimId") String claimId, @RequestBody String memberId);


}
