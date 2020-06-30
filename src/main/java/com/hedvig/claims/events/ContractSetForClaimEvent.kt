package com.hedvig.claims.events

import java.util.UUID


data class ContractSetForClaimEvent(
    val claimId: String,
    val contractId: UUID,
    val memberId: String
)
