package com.hedvig.homer.repository

import com.fasterxml.jackson.databind.JsonNode
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.annotation.Nullable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@TypeDef(
  name = "jsonb-node",
  typeClass = JsonNodeBinaryType::class
)
class SpeechToTextDao(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val id: Long = 0,
  @Type(type = "jsonb-node")
  @Column(columnDefinition = "jsonb")
  val response: JsonNode,
  @Nullable
  val transcript: String?,
  @Nullable
  val confidenceScore: Float?,
  @CreationTimestamp
  val createdAt: Instant = Instant.now(),
  @UpdateTimestamp
  var updatedAt: Instant = Instant.now()
){
}
