package com.hedvig.claims.events

import java.util.*

data class ContractIdAddedToClaimEvent(
    val memberId: String,
    val claimId: String,
    val contractId: UUID
)
