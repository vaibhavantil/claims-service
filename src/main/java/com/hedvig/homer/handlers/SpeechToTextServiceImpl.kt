package com.hedvig.homer.handlers


import com.google.cloud.speech.v1p1beta1.RecognitionAudio
import com.google.cloud.speech.v1p1beta1.SpeechClient
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult
import com.hedvig.homer.SpeechToTextService
import com.hedvig.homer.configuration.SpeechConfig
import com.hedvig.homer.repository.SpeechRecognitionResultDao
import com.hedvig.homer.repository.SpeechToTextDao
import com.hedvig.homer.repository.SpeechToTextRepository
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
class SpeechToTextServiceImpl(
  private val storageService: StorageServiceImpl,
  val speechConfig: SpeechConfig,
  val speechClient: SpeechClient,
  val speechToTextRepository: SpeechToTextRepository
) : SpeechToTextService {
  override fun convertSpeechToText(audioURL: String, requestId: String): SpeechToTextResult =
    speechClient.use { speechClient ->
      val filename: String = downloadFile(audioURL)
      val file = convert(filename)
      val uploadedRawAudio = storageService.uploadObjectAndGetUri(file.toPath())
      val audio = RecognitionAudio.newBuilder()
        .setUri(uploadedRawAudio)
        .build()

      val dao = speechToTextRepository.save(
        SpeechToTextDao().also {
          it.requestId = requestId
          it.aurioUri = uploadedRawAudio
        }
      )

      val response = speechClient.longRunningRecognizeAsync(speechConfig.speechClientConfig, audio)

      while (!response.isDone) {
        println("Waiting for response...")
        Thread.sleep(10000)
      }

      val results: List<SpeechRecognitionResult> = response.get().resultsList

      var finalTranscript: String = "";
      var averageConfidenceScore: Float = 0f;

      results.forEach { result ->
        val alternative = result.getAlternatives(0)
        logger.info("Transcription: ${alternative.transcript}]\n")
        finalTranscript += alternative.transcript + "\n"
        averageConfidenceScore += alternative.confidence
      }

      averageConfidenceScore /= results.count()

      if (averageConfidenceScore.isNaN()) {
        averageConfidenceScore = 0f
      }

      FileUtils.deleteQuietly(file)
      FileUtils.deleteQuietly(File(filename))

      dao.response = results.map { it -> SpeechRecognitionResultDao.from(it) }.toMutableList()
      dao.transcript = finalTranscript
      dao.confidenceScore = averageConfidenceScore

      speechToTextRepository.save(dao)
      return SpeechToTextResult(finalTranscript, averageConfidenceScore)
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
    val logger = LoggerFactory.getLogger(this::class.java)
    const val RATE: Int = 16000
  }
}
