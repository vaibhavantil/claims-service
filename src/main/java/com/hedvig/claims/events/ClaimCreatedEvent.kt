package com.hedvig.claims.events

import org.axonframework.serialization.Revision

@Revision("1.1")
data class ClaimCreatedEvent(
  val id: String,
  val userId: String,
  val audioURL: String
)
