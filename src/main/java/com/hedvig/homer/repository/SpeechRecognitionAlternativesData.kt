package com.hedvig.homer.repository

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SpeechRecognitionAlternativesData(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val speechRecognitionAlternativesDataId: Long = 0
) {
  @Column(columnDefinition = "TEXT")
  var transcript: String? = null
  var confidence: Float? = null

  companion object {
    fun from(dto: SpeechRecognitionAlternative): SpeechRecognitionAlternativesData {
      val temp = SpeechRecognitionAlternativesData()
      temp.transcript = dto.transcript
      temp.confidence = dto.confidence
      return temp
    }
  }
}
