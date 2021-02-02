package com.hedvig.claims.events

import java.time.LocalDateTime
import javax.money.MonetaryAmount

class ExpensePaymentAddedEvent(
    val id: String,
    val claimId: String,
    val date: LocalDateTime,
    val userId: String,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String?,
    val exGratia: Boolean,
    val handlerReference: String?
)
