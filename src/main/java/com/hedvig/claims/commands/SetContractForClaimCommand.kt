package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class SetContractForClaimCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String,
    val contractId: UUID
)
