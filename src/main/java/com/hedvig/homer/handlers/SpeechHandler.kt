package com.hedvig.claims.handlers

import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechRecognitionResult
import com.hedvig.homer.handlers.Uploader
import com.hedvig.homer.handlers.utils.LanguageCode
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

@Component
class SpeechHandler(
  val uploader: Uploader
) {
  fun convertSpeechToText(audioURL: String, languageCode: LanguageCode? = LanguageCode.SWEDISH): SpeechResult =
    SpeechClient.create().use { speechClient ->

      val config = RecognitionConfig.newBuilder()
        .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
        .setSampleRateHertz(RATE)
        .setLanguageCode(languageCode.toString())
        .setAudioChannelCount(1)
        .build()

      val filename: String = downloadFile(audioURL)

      val file = convert(filename)

      val uploadedRawAudio = uploader.uploadObjectAndGetUri(file.toPath())

      val audio = RecognitionAudio.newBuilder()
        .setUri(uploadedRawAudio)
        .build()

      val response = speechClient.recognize()

      while (!response.isDone) {
        println("Waiting for response...")
        Thread.sleep(10000)
      }

      val results: List<SpeechRecognitionResult> = response.get().resultsList

      var finalResult: String = "";
      var averageConfidence: Float = 0f;

      results.forEach { result ->
        val alternative = result.getAlternatives(0)
        logger.info("Transcription: ${alternative.transcript}]\n")
        finalResult += alternative.transcript + "\n"
        averageConfidence += alternative.confidence
      }

      averageConfidence /= results.count()

      if (averageConfidence.isNaN()) {
        averageConfidence = 0f
      }

      FileUtils.deleteQuietly(file)
      FileUtils.deleteQuietly(File(filename))

      return SpeechResult(finalResult, averageConfidence)
    }


  private fun convert(filename: String): File {
    val tempExecId = UUID.randomUUID().toString()
    val tempOutputFile = File.createTempFile("temp_$tempExecId", "_out.flac")

    val ffmpeg = FFmpeg("ffmpeg")
    val ffprobe = FFprobe("ffprobe")

    val builder = FFmpegBuilder()
      .setInput(filename)
      .overrideOutputFiles(true) // Override the output if it exists
      .addOutput(tempOutputFile.absolutePath)
      .setAudioSampleRate(RATE)
      .setAudioChannels(1)
      .done()

    val executor = FFmpegExecutor(ffmpeg, ffprobe)

    // Run a one-pass encode
    executor.createJob(builder).run()

    return tempOutputFile
  }

  private fun downloadFile(urlStr: String): String {
    val webUrl = URL(urlStr)

    val ending = urlStr.substring(urlStr.lastIndexOf(".") + 1)

    val tempExecId = UUID.randomUUID().toString()
    val tempInputFile = File.createTempFile("temp_$tempExecId", "_in.$ending")

    webUrl.openStream().use { `in` -> Files.copy(`in`, tempInputFile.toPath(), StandardCopyOption.REPLACE_EXISTING) }

    return tempInputFile.absolutePath
  }

  companion object {
    val logger = LoggerFactory.getLogger(SpeechHandler::class.java)
    const val RATE: Int = 16000
  }


  class SpeechResult(
    val text: String,
    val confidence: Float
  )
}
