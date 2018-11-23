package com.hedvig.claims;

import org.axonframework.config.EventHandlingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableFeignClients
public class ClaimsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClaimsApplication.class, args);
  }

  @Autowired
  public void configure(EventHandlingConfiguration config) {
    config.usingTrackingProcessors();
  }

  @Configuration
  @Profile("development")
  @ComponentScan(lazyInit = true)
  static class DevConfig {
  }
}
