package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class DeleteClaimFileCommand(
        val claimFileId: String,
        @TargetAggregateIdentifier
        val claimId: String,
        val deletedBy: String
)
