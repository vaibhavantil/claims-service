package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class MarkClaimFileAsDeletedCommand(
        val claimFileId: String,
        @TargetAggregateIdentifier
        val claimId: String,
        val deletedBy: String
)
