package com.hedvig.claims.events

import com.hedvig.claims.web.dto.ClaimDataType.DataType
import lombok.Data

import java.time.LocalDateTime

data class DataItemAddedEvent(
  var id: String,

  var claimsId: String,
  var date: LocalDateTime,
  var userId: String,

  var type: DataType,
  var name: String,
  var title: String,
  var received: Boolean?,
  var value: String
)
