package com.hedvig.claims.serviceIntegration.ticketService;

public interface TicketServiceClient {

  @FeignClient(value = "ticket-service", url = "${hedvig.ticket-service.url:ticket-service}")

  /*
    @PostMapping(
      value = "/v2/_/members/{memberId}/payout",
      method = POST)
    ResponseEntity<UUID> executePayment(@PathVariable(name = "memberId") String memberId,
                                        @RequestBody PayoutRequest request);


   */
}
