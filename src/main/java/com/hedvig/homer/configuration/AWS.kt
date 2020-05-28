package com.hedvig.homer.configuration

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class AWS {
  @Bean
  @Profile("production")
  fun s3ClientProd(credentialsProvider: AWSCredentialsProvider?): AmazonS3 {
    return AmazonS3Client.builder().withCredentials(credentialsProvider).build()
  }

  @Bean
  @Profile("staging")
  fun s3ClientStaging(credentialsProvider: AWSCredentialsProvider?): AmazonS3 {
    return AmazonS3Client.builder().withCredentials(credentialsProvider).build()
  }

  @Bean
  @Profile("development", "test")
  fun s3Client(credentialsProvider: AWSCredentialsProvider?): AmazonS3 {
    return AmazonS3Client.builder().withCredentials(credentialsProvider)
      .withRegion(Regions.EU_CENTRAL_1).build()
  }
}
