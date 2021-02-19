package com.hedvig.claims.config;

import com.hedvig.claims.events.upcast.*;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AxonConfiguration {
  @Bean
  public EventUpcasterChain eventUpcasters() {
    return new EventUpcasterChain(
      new ClaimCreatedEvent_v1(),
      new PaymentAddedEvent_v1(),
      new PaymentAddedEvent_v2(),
      new ClaimFileUploadedEventUpcaster_v1(),
      new AutomaticPaymentAddedEventUpcaster()
    );
  }

  @Autowired
  public void configure(EventProcessingConfiguration config) {
    config.usingTrackingProcessors();
    config.registerSubscribingEventProcessor("com.hedvig.claims.query");

      config.registerTrackingEventProcessor(
          "BackfillDateOfClaim",
          x -> TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
              .andInitialTrackingToken(StreamableMessageSource::createTailToken));
  }

  @Autowired
  @Profile("TicketService")
  public void configureTicketServiceListener(EventProcessingConfiguration config) {
    config.registerTrackingEventProcessor(
      "TicketService",
      x -> TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
        .andInitialTrackingToken(StreamableMessageSource::createHeadToken));
  }
}
