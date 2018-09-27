package com.hedvig.claims.sagas;

import com.hedvig.claims.commands.AddFailedAutomaticPaymentCommand;
import com.hedvig.claims.commands.AddInitiatedAutomaticPaymentCommand;
import com.hedvig.claims.events.AutomaticPaymentAddedEvent;
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
public class PayoutAddSaga {

  @Autowired
  transient PaymentService paymentService;

  @Autowired
  transient CommandGateway commandGateway;

  @StartSaga
  @SagaEventHandler(associationProperty = "memberId")
  @EndSaga
  public void on(AutomaticPaymentAddedEvent e) {
    PaymentResponse response = paymentService.executePayment(e.getMemberId(), e.getAmount());

    if (response.getTransactionStatus().equals(TransactionStatus.INITIATED)) {
      commandGateway.sendAndWait(
          new AddInitiatedAutomaticPaymentCommand(e.getId(), e.getClaimId(), e.getMemberId(),
              response.getTransactionReference(), response.getTransactionStatus()));
    } else {
      commandGateway
          .sendAndWait(new AddFailedAutomaticPaymentCommand(e.getId(), e.getClaimId(), e.getMemberId(),
              response.getTransactionStatus()));
    }
  }

}
