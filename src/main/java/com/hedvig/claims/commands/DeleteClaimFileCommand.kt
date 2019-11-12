package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class DeleteClaimFileCommand(
        val claimFileId: UUID,
        @TargetAggregateIdentifier
        val claimId: UUID
)
