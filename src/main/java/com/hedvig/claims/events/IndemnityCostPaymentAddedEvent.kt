package com.hedvig.claims.events

import java.time.LocalDateTime
import javax.money.MonetaryAmount

class IndemnityCostPaymentAddedEvent (
    var id: String,
    var claimId: String,
    var date: LocalDateTime,
    var userId: String,
    var amount: MonetaryAmount,
    var deductible: MonetaryAmount,
    var note: String?,
    var exGratia: Boolean,
    var handlerReference: String?
)
