package com.hedvig.claims.sagas;

import com.hedvig.claims.commands.FailPayoutCommand;
import com.hedvig.claims.commands.InitiatePayoutCommand;
import com.hedvig.claims.events.PayoutAddedEvent;
import com.hedvig.claims.serviceIntegration.paymentService.PaymentService;
import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse;
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class PaymentExecuteSaga {

  @Autowired
  transient PaymentService paymentService;

  @Autowired
  transient CommandGateway commandGateway;

  @StartSaga
  @SagaEventHandler(associationProperty = "memberId")
  @EndSaga
  public void on(PayoutAddedEvent e) {
    PaymentResponse response = paymentService.executePayment(e.getMemberId(), e.getAmount());

    if (response.getTransactionStatus().equals(TransactionStatus.INITIATED)) {
      commandGateway.sendAndWait(
          new InitiatePayoutCommand(e.getId(), e.getClaimId(), e.getMemberId(),
              response.getTransactionReference(), response.getTransactionStatus()));
    } else {
      commandGateway
          .sendAndWait(new FailPayoutCommand(e.getId(), e.getClaimId(), e.getMemberId(),
              response.getTransactionStatus()));
    }
  }

}
