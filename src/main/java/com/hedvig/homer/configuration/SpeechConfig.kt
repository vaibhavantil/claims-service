package com.hedvig.homer.configuration

import com.google.cloud.speech.v1p1beta1.RecognitionConfig
import com.google.cloud.speech.v1p1beta1.SpeechClient
import com.hedvig.homer.handlers.SpeechToTextServiceImpl
import com.hedvig.homer.handlers.utils.LanguageCode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpeechConfig {
    fun createSpeechClientConfig(nAlternatives: Int): RecognitionConfig =
        RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
            .setSampleRateHertz(SpeechToTextServiceImpl.RATE)
            .setLanguageCode(LanguageCode.SWEDISH.value)
            .addAllAlternativeLanguageCodes(supportedLanguages)
            .setMaxAlternatives(nAlternatives)
            .setAudioChannelCount(1)
            .build()

    @Bean(destroyMethod = "close")
    fun createSpeechClient(): SpeechClient {
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
