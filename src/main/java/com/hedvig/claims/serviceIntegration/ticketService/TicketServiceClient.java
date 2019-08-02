package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.CreateTicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")

public interface TicketServiceClient {

  @PostMapping( value="/_/tickets/new/")
    ResponseEntity<String> createNewTicket (@RequestBody CreateTicketDto ticket ) ;

}
