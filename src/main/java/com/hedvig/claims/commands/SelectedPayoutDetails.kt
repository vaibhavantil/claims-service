package com.hedvig.claims.commands

import com.hedvig.claims.events.PayoutDetails
import com.hedvig.claims.web.dto.SelectedPayoutDetails as SelectedPayoutDetailsRequest

sealed class SelectedPayoutDetails {

    abstract val type: String

    data class Swish(
        val phoneNumber: String,
        val ssn: String,
        val message: String
    ) : SelectedPayoutDetails() {
        override val type = "swish"
    }

    object NotSelected : SelectedPayoutDetails() {
        override val type = "notSelected"
    }

    companion object {
        fun toEvent(selectedPayoutDetails: SelectedPayoutDetails) = when (selectedPayoutDetails) {
            is Swish -> PayoutDetails.Swish(
                selectedPayoutDetails.phoneNumber,
                selectedPayoutDetails.ssn,
                selectedPayoutDetails.message
            )
            NotSelected -> PayoutDetails.NotSelected
        }

        fun fromEvent(payoutDetails: PayoutDetails) = when (payoutDetails) {
            is PayoutDetails.Swish -> SelectedPayoutDetails.Swish(
                payoutDetails.phoneNumber,
                payoutDetails.ssn,
                payoutDetails.message
            )
            PayoutDetails.NotSelected -> SelectedPayoutDetails.NotSelected
        }

        fun fromRequest(selectedPayoutDetails: SelectedPayoutDetailsRequest, ssn: String) = when (selectedPayoutDetails) {
            is SelectedPayoutDetailsRequest.Swish -> Swish(
                selectedPayoutDetails.phoneNumber,
                ssn,
                selectedPayoutDetails.message
            )
            SelectedPayoutDetailsRequest.NotSelected -> NotSelected
        }
    }
}
