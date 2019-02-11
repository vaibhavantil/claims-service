package com.hedvig.claims.events

import javax.money.MonetaryAmount
import lombok.AllArgsConstructor
import lombok.Value

data class AutomaticPaymentAddedEvent (
  val Id: String,
  val claimId: String,
  val memberId: String,
  val amount: MonetaryAmount,
  val deductible: MonetaryAmount,
  val note: String,
  val isExGracia: Boolean,
  val handlerReference: String,
  val sanctionCheckSkipped: Boolean
)
