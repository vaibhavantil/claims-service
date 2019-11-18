package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class SetClaimFileCategoryCommand (
        val claimFileId: String,
        @TargetAggregateIdentifier
        val claimId: String,
        val category: String
)
