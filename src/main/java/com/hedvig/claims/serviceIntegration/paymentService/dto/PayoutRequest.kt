package com.hedvig.claims.serviceIntegration.paymentService.dto

import com.hedvig.claims.web.dto.SelectedPayoutDetails
import javax.money.MonetaryAmount

class PayoutRequest(
    val amount: MonetaryAmount,
    val sanctionBypassed: Boolean,
    val referenceId: String?,
    val note: String?,
    val handler: String?,
    val payoutDetails: SelectedPayoutDetails?
) {
    val category = "CLAIM"
}
