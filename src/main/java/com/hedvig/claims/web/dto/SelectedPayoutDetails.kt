package com.hedvig.claims.web.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.claims.events.PayoutDetails

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(
        value = SelectedPayoutDetails.Swish::class,
        name = "swish"
    ),
    JsonSubTypes.Type(
        value = SelectedPayoutDetails.NotSelected::class,
        name = "notSelected"
    ),
)
sealed class SelectedPayoutDetails {

    data class Swish(
        val phoneNumber: String,
        val ssn: String,
        val message: String
    ) : SelectedPayoutDetails()

    object NotSelected : SelectedPayoutDetails()

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
    }
}
