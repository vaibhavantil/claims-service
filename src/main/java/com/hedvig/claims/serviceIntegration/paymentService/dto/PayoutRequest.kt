package com.hedvig.claims.serviceIntegration.paymentService.dto

import com.hedvig.claims.commands.SelectedPayoutDetails
import com.hedvig.claims.query.Carrier
import javax.money.MonetaryAmount

data class PayoutRequest(
    val amount: MonetaryAmount,
    val category: TransactionCategory = TransactionCategory.CLAIM,
    val sanctionBypassed: Boolean,
    val carrier: Carrier,
    val handler: String,
    val payoutDetails: SelectedPayoutDetails?
)
