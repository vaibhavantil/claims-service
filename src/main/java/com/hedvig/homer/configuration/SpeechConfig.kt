package com.hedvig.homer.configuration

import com.google.cloud.speech.v1p1beta1.RecognitionConfig
import com.google.cloud.speech.v1p1beta1.SpeechClient
import com.hedvig.homer.handlers.SpeechHandler
import com.hedvig.homer.handlers.utils.LanguageCode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpeechConfig {
  val speechClientConfig = RecognitionConfig.newBuilder()
    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
    .setSampleRateHertz(SpeechHandler.RATE)
    .setLanguageCode(LanguageCode.SWEDISH.value)
    .addAllAlternativeLanguageCodes(supportedLanguages)
    .setAudioChannelCount(1)
    .build()

  @Bean
  open fun createSpeechClient() : SpeechClient {
    return SpeechClient.create()
  }

  companion object {
    val supportedLanguages: ArrayList<String> = arrayListOf(
      LanguageCode.NORWEGIAN.value,
      LanguageCode.AMERICAN_ENGLISH.value,
      LanguageCode.GREEK.value
    )
  }
}
