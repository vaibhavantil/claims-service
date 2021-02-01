package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import javax.money.MonetaryAmount

data class AddIndemnityCostPaymentCommand(
    val id: String,
    @TargetAggregateIdentifier var claimID: String,
    var date: LocalDateTime,
    var amount: MonetaryAmount,
    var deductible: MonetaryAmount,
    var note: String?,
    var exGratia: Boolean,
    var handlerReference: String?
)
