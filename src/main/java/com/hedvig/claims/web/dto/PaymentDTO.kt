package com.hedvig.claims.web.dto

data class PaymentDTO(
    val claimID: String,
    val amount: Double,
    val deductible: Double,
    val note: String,
    val type: PaymentType,
    val exGratia: Boolean,
    val sanctionListSkipped: Boolean,
    val handlerReference: String
)
