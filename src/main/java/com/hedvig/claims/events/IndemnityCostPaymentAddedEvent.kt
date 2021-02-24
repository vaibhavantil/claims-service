package com.hedvig.claims.events

import com.hedvig.claims.query.Carrier
import org.axonframework.serialization.Revision
import javax.money.MonetaryAmount

@Revision("1.0")
class IndemnityCostPaymentAddedEvent (
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
