package com.hedvig.homer.repository

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SpeechRecognitionAlternativesDao(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val speechRecognitionAlternativesDaoId: Long = 0
) {
  var transcript: String? = null
  var confidence: Float? = null

  companion object {
    fun from(dto: SpeechRecognitionAlternative): SpeechRecognitionAlternativesDao {
      val temp = SpeechRecognitionAlternativesDao()
      temp.transcript = dto.transcript
      temp.confidence = dto.confidence
      return temp
    }
  }
}
