package com.hedvig.claims.web.dto

import com.hedvig.claims.query.Carrier
import javax.money.MonetaryAmount

data class CreatePaymentDto(
    val claimId: String,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String,
    val exGratia: Boolean,
    val type: PaymentType,
    val handlerReference: String,
    val sanctionListSkipped: Boolean,
    val carrier: Carrier
)
