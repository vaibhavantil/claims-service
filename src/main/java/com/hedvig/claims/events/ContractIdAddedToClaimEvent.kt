package com.hedvig.claims.events

import java.util.UUID


data class ContractIdAddedToClaimEvent(
    val contractId: UUID,
    val memberId: String,
    val claimId: String
)
