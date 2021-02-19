package com.hedvig.claims.events

sealed class PayoutDetails {
    data class Swish(
        val phoneNumber: String,
        val ssn: String,
        val message: String
    ) : PayoutDetails()

    object NotSelected : PayoutDetails()
}
