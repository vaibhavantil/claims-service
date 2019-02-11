package com.hedvig.claims.events

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import java.time.LocalDateTime

data class ClaimStatusUpdatedEvent(
  val claimsId: String,
  val userId: String,
  val registrationDate: LocalDateTime,
  val state: ClaimStates
)
