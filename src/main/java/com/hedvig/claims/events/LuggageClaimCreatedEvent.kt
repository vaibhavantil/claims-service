package com.hedvig.claims.events

import com.hedvig.claims.commands.CreateLuggageClaimCommand
import org.axonframework.commandhandling.model.AggregateIdentifier
import java.time.Instant
import java.time.LocalDateTime

data class LuggageClaimCreatedEvent(
        @AggregateIdentifier val id: String,
        val memberId: String,
        val date: LocalDateTime,
        val from: String,
        val to: String,
        val hoursDelayed: Int,
        val reference: String?,
        val timestamp: Instant
) {
    companion object {
        @JvmStatic
        fun from(command: CreateLuggageClaimCommand) = LuggageClaimCreatedEvent(
                command.id,
                command.memberId,
                command.date,
                command.from,
                command.to,
                command.hoursDelayed,
                command.reference,
                command.timestamp
        )
    }
}
