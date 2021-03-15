package com.hedvig.claims.serviceIntegration.paymentService

import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest
import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

@Service
class PaymentServiceImpl(
    private val paymentServiceClient: PaymentServiceClient
) : PaymentService {
    override fun executePayment(memberId: String, request: PayoutRequest): PaymentResponse {
        return try {
            val response = paymentServiceClient.executePayment(memberId, request)
            if (response.statusCode.is2xxSuccessful) PaymentResponse(
                transactionReference = response.body!!,
                transactionStatus = TransactionStatus.INITIATED
            ) else PaymentResponse(response.body!!, TransactionStatus.NOT_ACCEPTED)
        } catch (exception: RestClientResponseException) {
            return when (exception.rawStatusCode) {
                404 -> PaymentResponse(null, TransactionStatus.NOT_ACCEPTED)
                403 -> PaymentResponse(null, TransactionStatus.FORBIDDEN)
                else -> PaymentResponse(null, TransactionStatus.FAILED)
            }
        }
    }
}
