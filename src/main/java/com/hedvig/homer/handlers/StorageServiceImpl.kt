package com.hedvig.homer.handlers

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.hedvig.homer.configuration.StorageConfig
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@Component
class StorageServiceImpl(
  val storage: Storage,
  val storageConfig: StorageConfig
) : StorageService {
  override fun uploadObjectAndGetUri(filePath: Path): String {
    val objectName = "${UUID.randomUUID()}_raw_audio.flac"

    val blobId: BlobId = BlobId.of(storageConfig.rawAudioBucketName, objectName)
    val blobInfo: BlobInfo = BlobInfo.newBuilder(blobId).setContentType("application/audio").build()

    val uploadedBlob = storage.create(blobInfo, Files.readAllBytes(filePath))

    if (uploadedBlob.exists()) {
      return "gs://${storageConfig.rawAudioBucketName}/$objectName"
    }

    throw RuntimeException("Failed to upload $objectName to ${storageConfig.rawAudioBucketName}")
  }
}
