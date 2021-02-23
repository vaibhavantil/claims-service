package com.hedvig.claims.serviceIntegration.paymentService

import com.hedvig.claims.serviceIntegration.paymentService.dto.PaymentResponse
import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest

interface PaymentService {
    fun executePayment(memberId: String, request: PayoutRequest): PaymentResponse
}
