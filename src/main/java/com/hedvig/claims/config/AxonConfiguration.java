package com.hedvig.claims.config;

import com.hedvig.claims.events.upcast.ClaimCreatedEvent_v1;
import com.hedvig.claims.events.upcast.PaymentAddedEvent_v1;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfiguration {
  @Bean
  public EventUpcasterChain eventUpcasters() {
    return new EventUpcasterChain(
      new ClaimCreatedEvent_v1(),
      new PaymentAddedEvent_v1()
    );
  }

  @Autowired
  public void configure(EventProcessingConfiguration config) {
    config.usingTrackingProcessors();
    config.registerSubscribingEventProcessor("com.hedvig.claims.query");

    config.registerTrackingEventProcessor(
      "TicketService",
        x -> TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
        .andInitialTrackingToken(StreamableMessageSource::createHeadToken));
  }
}
