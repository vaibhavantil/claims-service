package com.hedvig.claims.sagas

import com.hedvig.claims.commands.AddFailedAutomaticPaymentCommand
import com.hedvig.claims.commands.AddInitiatedAutomaticPaymentCommand
import com.hedvig.claims.events.AutomaticPaymentAddedEvent
import com.hedvig.claims.serviceIntegration.paymentService.PaymentService
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
class PayoutAddSaga {
    @Autowired
    @Transient
    lateinit var paymentService: PaymentService

    @Autowired
    @Transient
    lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "memberId")
    @EndSaga
    fun on(event: AutomaticPaymentAddedEvent) {
        val response = paymentService.executePayment(
            memberId = event.memberId,
            request = PayoutRequest(
                amount = event.amount,
                sanctionBypassed = event.isSanctionCheckSkipped,
                carrier = event.carrier,
                handler = event.handlerReference
            )
        )
        if (response.transactionStatus == TransactionStatus.INITIATED) {
            commandGateway.sendAndWait<Void>(
                AddInitiatedAutomaticPaymentCommand(
                    id = event.Id,
                    claimId = event.claimId,
                    memberId = event.memberId,
                    transactionReference = response.transactionReference!!,
                    transactionStatus = response.transactionStatus
                )
            )
        } else {
            commandGateway.sendAndWait<Void>(
                AddFailedAutomaticPaymentCommand(
                    id = event.Id,
                    claimId = event.claimId,
                    memberId = event.memberId,
                    transactionStatus = response.transactionStatus
                )
            )
        }
    }
}
