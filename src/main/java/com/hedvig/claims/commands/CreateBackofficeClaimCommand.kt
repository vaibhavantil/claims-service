package com.hedvig.claims.commands

import com.hedvig.claims.aggregates.ClaimSource
import lombok.Value
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.Instant
import java.util.UUID

class CreateBackofficeClaimCommand(
    @TargetAggregateIdentifier
    val id: String,
    val memberId: String,
    val registrationDate: Instant,
    val claimSource: ClaimSource,
    val contractId: UUID?
)
