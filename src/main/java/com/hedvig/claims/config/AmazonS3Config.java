package com.hedvig.claims.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

  @Bean
  AmazonS3 s3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3Client.builder().withCredentials(credentialsProvider)
    .withRegion(Regions.EU_CENTRAL_1).build();
  }

  @Bean
  AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }
}
