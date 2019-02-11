package com.hedvig.claims.events

import com.hedvig.claims.aggregates.ClaimSource
import java.time.Instant

data class BackofficeClaimCreatedEvent(
  val id: String,
  val memberId: String,
  val registrationDate: Instant,
  val claimSource: ClaimSource
)
