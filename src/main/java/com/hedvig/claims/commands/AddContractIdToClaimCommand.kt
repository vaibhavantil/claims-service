package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class AddContractIdToClaimCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String,
    val contractId: UUID
)
