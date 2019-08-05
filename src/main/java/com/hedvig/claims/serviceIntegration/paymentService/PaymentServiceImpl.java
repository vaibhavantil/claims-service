package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest;
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import java.util.UUID;

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
  public PaymentResponse executePayment(String memberId, PayoutRequest request) {
    try {
      ResponseEntity<UUID> response = paymentServiceClient.executePayment(memberId, request);

      return response.getStatusCode().is2xxSuccessful() ? new PaymentResponse(response.getBody(),
        TransactionStatus.INITIATED)
        : new PaymentResponse(response.getBody(), TransactionStatus.NOT_ACCEPTED);
    } catch (RestClientResponseException ex) {
      if (ex.getRawStatusCode() == 404) {
        return new PaymentResponse(null, TransactionStatus.NOT_ACCEPTED);
      }
      if (ex.getRawStatusCode() == 403) {
        return new PaymentResponse(null, TransactionStatus.FORBIDDEN);
      }
      return new PaymentResponse(null, TransactionStatus.FAILED);
    }
  }
}
