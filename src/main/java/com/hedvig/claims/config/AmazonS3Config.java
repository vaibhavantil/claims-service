package com.hedvig.claims.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {
  @Bean
  AmazonS3 s3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3Client.builder().withCredentials(credentialsProvider).build();
  }
}
