package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//TODO("Change URL!!!!!")
@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")
//@FeignClient(value = "ticket-service", url = "${tickets.baseUrl}")

public interface TicketServiceClient {

  @PostMapping( value="/_/tickets/new/")
    ResponseEntity<String> createNewTicket (@RequestBody TicketDto ticket ) ;

}
