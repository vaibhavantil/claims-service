package com.hedvig.claims.events

import com.hedvig.claims.query.Carrier
import org.axonframework.serialization.Revision
import javax.money.MonetaryAmount

@Revision("3.0")
class AutomaticPaymentAddedEvent(
    val Id: String,
    val claimId: String,
    val memberId: String,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String,
    val isExGracia: Boolean,
    val handlerReference: String,
    val sanctionCheckSkipped: Boolean,
    var carrier: Carrier,
    val payoutDetails : PayoutDetails
)

