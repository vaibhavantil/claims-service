package com.hedvig.claims.serviceIntegration.paymentService;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "payment-service", url = "${hedvig.payment-service.url:payment-service}")
public interface PaymentServiceClient {

  @RequestMapping(
    value = "/v2/_/members/{memberId}/payout",
    method = POST)
  ResponseEntity<UUID> executePayment(@PathVariable(name = "memberId") String memberId,
    @RequestBody PayoutRequest request);
}
