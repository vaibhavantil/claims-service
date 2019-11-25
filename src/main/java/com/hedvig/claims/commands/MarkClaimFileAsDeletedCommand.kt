package com.hedvig.claims.commands

import java.util.UUID
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class MarkClaimFileAsDeletedCommand(
    val claimFileId: UUID,
    @TargetAggregateIdentifier
    val claimId: String,
    val deletedBy: String
)
