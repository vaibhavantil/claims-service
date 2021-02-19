package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import javax.money.MonetaryAmount

class AddAutomaticPaymentCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String,
    val amount: MonetaryAmount,
    val deductible: MonetaryAmount,
    val note: String?,
    val isExGracia: Boolean,
    val handlerReference: String,
    val sanctionCheckSkipped: Boolean,
    val payoutDetails: SelectedPayoutDetails
)
