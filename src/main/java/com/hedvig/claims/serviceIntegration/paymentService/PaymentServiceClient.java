package com.hedvig.claims.serviceIntegration.paymentService;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "payment-service", url = "${hedvig.payment-service.url:payment-service}")
public interface PaymentServiceClient {

  @RequestMapping(
      value = "/_/members/{memberId}/payout",
      method = POST)
  ResponseEntity<?> executePayment(@RequestBody PayoutRequest requestBody);
}
