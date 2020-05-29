package com.hedvig.homer.repository

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.annotation.Nullable
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class SpeechToText(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val id: Long = 0
) {
  @Nullable
  var requestId: String? = null

  @Nullable
  var aurioUri: String? = null

  @OneToMany(cascade = [(CascadeType.ALL)])
  var response: MutableList<SpeechRecognitionResultData> = arrayListOf()

  @Nullable
  @Column(columnDefinition = "TEXT")
  var transcript: String? = null

  @Nullable
  var confidenceScore: Float? = null

  @CreationTimestamp
  var createdAt: Instant = Instant.now()

  @UpdateTimestamp
  var updatedAt: Instant = Instant.now()
}
