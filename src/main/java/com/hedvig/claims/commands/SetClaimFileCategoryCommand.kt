package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class SetClaimFileCategoryCommand (
        val claimFileId: UUID,
        @TargetAggregateIdentifier
        val claimId: String,
        val category: String
)
