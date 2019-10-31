package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.Instant
import java.time.LocalDateTime

data class CreateLuggageClaimCommand(
        @TargetAggregateIdentifier
        val id: String,
        val memberId: String,
        val date: LocalDateTime,
        val from: String,
        val to: String,
        val hoursDelayed: Int,
        val reference: String?,
        val timestamp: Instant
)
