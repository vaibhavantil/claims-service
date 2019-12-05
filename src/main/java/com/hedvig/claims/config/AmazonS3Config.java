package com.hedvig.claims.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class AmazonS3Config {

  @Bean
  @Profile("development")
  AmazonS3 s3Client(AWSCredentialsProvider credentialsProvider) {
    log.info("creating amazon s3Client bean");
    return AmazonS3Client.builder().withCredentials(credentialsProvider)
    .withRegion(Regions.EU_CENTRAL_1).build();
  }

  @Bean
  @Profile("staging")
  AmazonS3 s3ClientStaging(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3Client.builder().withCredentials(credentialsProvider).build();
  }

  @Bean
  @Profile("production")
  AmazonS3 s3ClientProd(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3Client.builder().withCredentials(credentialsProvider).build();
  }

  @Bean
  AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }
}
