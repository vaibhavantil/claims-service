package com.hedvig.claims.events

import java.util.UUID


data class ContractSetForClaimEvent(
    val contractId: UUID,
    val memberId: String,
    val claimId: String
)
