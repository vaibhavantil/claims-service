package com.hedvig.claims.events

data class SetDefaultDateOfLossEvent(
    val claimId: String,
    val memberId: String
)
