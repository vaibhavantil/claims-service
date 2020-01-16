package com.hedvig.claims.handlers

import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechRecognitionResult
import com.google.protobuf.ByteString
import com.hedvig.claims.handlers.utils.LanguageCode
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.util.UUID


@Component
class SpeechHandler {
  fun convertSpeechToText(audioURL: String, languageCode: LanguageCode? = LanguageCode.SWEDISH): String {
    SpeechClient.create().use { speechClient ->

      val config = RecognitionConfig.newBuilder()
        .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
        .setSampleRateHertz(RATE)
        .setLanguageCode(languageCode.toString())
        .build()

      val fileName = ""

      val file = convert(fileName)
      val data = Files.readAllBytes(file.toPath())
      val audioBytes = ByteString.copyFrom(data)

      val audio = RecognitionAudio.newBuilder()
        .setContent(audioBytes)
        .build()

      val response = speechClient.longRunningRecognizeAsync(config, audio)

      while (!response.isDone) {
        println("Waiting for response...")
        Thread.sleep(10000)
      }

      val results: List<SpeechRecognitionResult> = response.get().resultsList

      var finalResult: String = ""

      results.forEach { result ->
        val alternative = result.getAlternatives(0)
        logger.info("Transcription: ${alternative.transcript}]\n")
        finalResult += alternative.transcript
      }
      return finalResult
    }
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

  private fun downloadFile(urlStr: String, file: String) {
    val url = URL(urlStr)
    val bis = BufferedInputStream(url.openStream())
    val fis = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var count = 0
    while (count != -1) {
      fis.write(buffer, 0, count)
      count = bis.read(buffer, 0, 1024)
    }
    fis.close()
    bis.close()
  }

  companion object {
    val logger = LoggerFactory.getLogger(SpeechHandler::class.java)
    const val RATE: Int = 16000
  }
}
