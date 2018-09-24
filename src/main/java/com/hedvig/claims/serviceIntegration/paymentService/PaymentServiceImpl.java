package com.hedvig.claims.serviceIntegration.paymentService;

import java.util.Optional;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class PaymentServiceImpl implements PaymentService {

  private PaymentServiceClient paymentServiceClient;

  public PaymentServiceImpl(
      PaymentServiceClient paymentServiceClient) {
    this.paymentServiceClient = paymentServiceClient;
  }

  @Override
  public Optional<UUID> executePayment(String memberId, MonetaryAmount amount) {
    try {
      ResponseEntity<UUID> response = paymentServiceClient.executePayment(memberId, amount);
      return response.getStatusCode().is2xxSuccessful() ? Optional.of(response.getBody())
          : Optional.empty();
    } catch (RestClientResponseException ex) {
      return Optional.empty();
    }
  }
}
