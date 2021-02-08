package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import javax.money.MonetaryAmount

data class AddIndemnityCostPaymentCommand(
    val id: String,
    @TargetAggregateIdentifier val claimId: String,
    val date: LocalDateTime,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String?,
    val exGratia: Boolean,
    val handlerReference: String?
)
