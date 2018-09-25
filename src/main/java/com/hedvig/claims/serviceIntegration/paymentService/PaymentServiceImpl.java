package com.hedvig.claims.serviceIntegration.paymentService;

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutStatus;
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
  public PayoutResponse executePayment(String memberId, MonetaryAmount amount) {
    try {
      ResponseEntity<UUID> response = paymentServiceClient.executePayment(memberId, amount);

      return response.getStatusCode().is2xxSuccessful() ? new PayoutResponse(response.getBody(),
          PayoutStatus.INITIATED)
          : new PayoutResponse(response.getBody(), PayoutStatus.NOTACCEPTED);
    } catch (RestClientResponseException ex) {
      if (ex.getRawStatusCode() == 404) {
        return new PayoutResponse(null, PayoutStatus.NOTACCEPTED);
      }
      if (ex.getRawStatusCode() == 403) {
        return new PayoutResponse(null, PayoutStatus.FORBIDDEN);
      }
      return new PayoutResponse(null, PayoutStatus.FAILED);
    }
  }
}
