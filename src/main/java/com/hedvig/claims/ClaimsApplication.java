package com.hedvig.claims;

import org.axonframework.config.EventHandlingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClaimsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClaimsApplication.class, args);
  }

  @Autowired
  public void configure(EventHandlingConfiguration config) {
    config.usingTrackingProcessors();
  }
}
