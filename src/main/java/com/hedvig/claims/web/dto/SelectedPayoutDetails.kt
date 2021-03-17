package com.hedvig.claims.web.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

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
        val message: String
    ) : SelectedPayoutDetails()

    object NotSelected : SelectedPayoutDetails()
}
