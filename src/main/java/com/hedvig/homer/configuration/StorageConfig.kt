package com.hedvig.homer.configuration

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google.storage")
open class StorageConfig {

  lateinit var projectId: String

  lateinit var rawAudioBucketName: String

  @Bean
  open fun createStorage(): Storage {
    return StorageOptions.newBuilder().setProjectId(projectId).build().getService()
  }
}
