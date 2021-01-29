package com.hedvig.claims.web.dto

import java.util.UUID
import javax.money.MonetaryAmount

data class PaymentRequestDTO(
    val claimId: UUID,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val handlerReference: String,
    val sanctionCheckSkipped: Boolean,
    val paymentRequestNote: String?,
    val exGratia: Boolean
)
