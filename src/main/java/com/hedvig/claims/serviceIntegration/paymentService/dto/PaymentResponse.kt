package com.hedvig.claims.serviceIntegration.paymentService.dto

import java.util.UUID

data class PaymentResponse(
    val transactionReference: UUID?,
    val transactionStatus: TransactionStatus
)
