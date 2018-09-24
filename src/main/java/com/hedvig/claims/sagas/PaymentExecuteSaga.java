package com.hedvig.claims.sagas;

import com.hedvig.claims.events.PaymentExecutedEvent;
import com.hedvig.claims.serviceIntegration.paymentService.PaymentService;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class PaymentExecuteSaga {

  @Autowired
  transient PaymentService paymentService;

  @StartSaga
  @SagaEventHandler(associationProperty = "memberId")
  @EndSaga
  public void on(PaymentExecutedEvent e) {
    paymentService.executePayment(e.getMemberId(), e.getAmount());
  }

}
