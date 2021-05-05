package com.hedvig.homer.handlers


import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.google.cloud.speech.v1p1beta1.RecognitionAudio
import com.google.cloud.speech.v1p1beta1.SpeechClient
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult
import com.hedvig.homer.SpeechToTextService
import com.hedvig.homer.configuration.SpeechConfig
import com.hedvig.homer.repository.SpeechRecognitionResultData
import com.hedvig.homer.repository.SpeechToText
import com.hedvig.homer.repository.SpeechToTextRepository
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

@Component
class SpeechToTextServiceImpl(
    private val storageService: StorageServiceImpl,
    val speechConfig: SpeechConfig,
    val speechToTextRepository: SpeechToTextRepository,
    val amazonS3: AmazonS3,
    val speechClient: SpeechClient,
    @Value("\${claims.voiceRecordingBucketName}")
    val bucketName: String
) : SpeechToTextService {

    @Throws
    override fun convertSpeechToText(audioURL: String, requestId: String, nAlternatives: Int): SpeechToTextResult {
        val filename: String = extractFileFromURL(audioURL)
        val file = convert(filename)
        val uploadedRawAudio = storageService.uploadObjectAndGetUri(file.toPath())
        val audio = RecognitionAudio.newBuilder()
            .setUri(uploadedRawAudio)
            .build()

        val dao = speechToTextRepository.save(
            SpeechToText().also {
                it.requestId = requestId
                it.aurioUri = uploadedRawAudio
            }
        )

        var finalTranscript = ""
        var addedConfidenceScore = 0f
        var languageCode = ""
        val alternativesList: MutableList<String> = mutableListOf()

        val speechClientConfig = speechConfig.createSpeechClientConfig(nAlternatives)

        val response = speechClient.longRunningRecognizeAsync(speechClientConfig, audio)

        val results: List<SpeechRecognitionResult> = response.get().resultsList

         results.forEach { result ->
            val bestAlternative = result.getAlternatives(0)
            logger.info("Best transcription: ${bestAlternative.transcript}\n")
            result.alternativesList.forEach { alternative ->
                logger.info("Alternative transcription: ${alternative.transcript}\n")
                alternativesList.add(alternative.transcript)
            }
            finalTranscript += bestAlternative.transcript + "\n"
            addedConfidenceScore += bestAlternative.confidence

            if (!languageCode.contains(result.languageCode)) {
                if (languageCode.isEmpty()) {
                    languageCode = result.languageCode
                } else {
                    languageCode += ", ${result.languageCode}"
                }
            }
        }

        if (addedConfidenceScore.isNaN()) {
            addedConfidenceScore = 0f
        }

        FileUtils.deleteQuietly(file)
        FileUtils.deleteQuietly(File(filename))

        val averageConfidenceScore = if (results.isNotEmpty()) addedConfidenceScore / results.size else 0f

        dao.response = results.map { SpeechRecognitionResultData.from(it) }.toMutableList()
        dao.transcript = finalTranscript
        dao.confidenceScore = averageConfidenceScore

        speechToTextRepository.save(dao)
        return SpeechToTextResult(finalTranscript, averageConfidenceScore, languageCode, alternativesList)
    }

    private fun extractFileFromURL(audioURL: String): String {
        val split: Array<String> = audioURL.split("/".toRegex()).toTypedArray()
        val key = split[split.size - 1]

        val preSignedUrl = amazonS3.generatePresignedUrl(
        bucketName,
        key,
        Date(Instant.now().plus(30, ChronoUnit.MINUTES).toEpochMilli()),
        HttpMethod.GET
        ).toString()
        return downloadFile(preSignedUrl)
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
