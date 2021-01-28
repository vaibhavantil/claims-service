package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime

data class AddPaymentCommand(
    val  id: String,
    @TargetAggregateIdentifier
    val  claimID: String,
    val  date: LocalDateTime,
    val  userId: String,
    val  amount: Double,
    val  deductible: Double,
    val  note: String,
    val  payoutDate: LocalDateTime,
    val  exGratia: Boolean,
    val  handlerReference: String
)
