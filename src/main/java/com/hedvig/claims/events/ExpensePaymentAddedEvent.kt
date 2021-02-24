package com.hedvig.claims.events

import com.hedvig.claims.query.Carrier
import javax.money.MonetaryAmount
import org.axonframework.serialization.Revision

@Revision("2.0")
class ExpensePaymentAddedEvent(
    val id: String,
    val claimId: String,
    val userId: String,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String?,
    val exGratia: Boolean,
    val handlerReference: String?,
    val carrier: Carrier
)
