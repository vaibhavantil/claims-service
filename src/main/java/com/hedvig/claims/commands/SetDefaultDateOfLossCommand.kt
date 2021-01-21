package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class SetDefaultDateOfLossCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String
)
