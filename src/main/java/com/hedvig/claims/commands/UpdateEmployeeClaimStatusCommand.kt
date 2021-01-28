package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class UpdateEmployeeClaimStatusCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val isCoveringEmployee: Boolean
)
