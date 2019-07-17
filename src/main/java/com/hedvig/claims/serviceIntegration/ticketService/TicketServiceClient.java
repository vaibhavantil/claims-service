package com.hedvig.claims.serviceIntegration.ticketService;

import com.hedvig.claims.serviceIntegration.ticketService.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

//TODO("Change URL!!!!!")
//@FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service
@FeignClient(value = "tickets", url = "${tickets.baseUrl}")

public interface TicketServiceClient {

  @PostMapping( value="/_/tickets/claim/{claimId}")
    ResponseEntity<String> createNewTicket (@PathVariable (name = "claimId") String claimId,
                                  @RequestBody TicketDto ticket
                                 ) ;

}
