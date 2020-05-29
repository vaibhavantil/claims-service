package com.hedvig.homer.repository

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class SpeechRecognitionResultData(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val speechRecognitionResultDataId: Long = 0
) {

  @OneToMany(cascade = [(CascadeType.ALL)])
  var listOfAlternatives: MutableList<SpeechRecognitionAlternativesData> = arrayListOf()
  var languageCode: String? = null

  companion object {
    fun from(dto: SpeechRecognitionResult): SpeechRecognitionResultData {
      val temp = SpeechRecognitionResultData()
      temp.languageCode = dto.languageCode
      temp.listOfAlternatives = dto.alternativesList.map { SpeechRecognitionAlternativesData.from(it) }.toMutableList()
      return temp
    }
  }
}
