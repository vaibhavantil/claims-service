package com.hedvig.claims.events

import java.time.LocalDateTime
import lombok.Value
import org.axonframework.serialization.Revision

@Revision("1.0")
data class PaymentAddedEvent (
  val id: String,
  val claimsId: String,
  val date: LocalDateTime,
  val userId: String,

  val amount: Double,
  val deductible: Double,
  val note: String,
  val payoutDate: LocalDateTime,
  val exGratia: Boolean,
  val handlerReference: String
)
