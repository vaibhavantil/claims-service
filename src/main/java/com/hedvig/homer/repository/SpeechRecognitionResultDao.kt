package com.hedvig.homer.repository

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class SpeechRecognitionResultDao(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  val speechRecognitionResultDaoId: Long = 0
) {

  @OneToMany(cascade = [(CascadeType.ALL)])
  var listOfAlternatives: MutableList<SpeechRecognitionAlternativesDao> = arrayListOf()
  var languageCode: String? = null

  companion object {
    fun from(dto: SpeechRecognitionResult): SpeechRecognitionResultDao {
      val temp = SpeechRecognitionResultDao()
      temp.languageCode = dto.languageCode
      temp.listOfAlternatives = dto.alternativesList.map { SpeechRecognitionAlternativesDao.from(it) }.toMutableList()
      return temp
    }
  }
}
