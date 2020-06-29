package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class AddContractIdToClaimCommand(
    val memberId: String,
    @TargetAggregateIdentifier
    val claimId: String,
    val contractId: UUID
)
