package com.hedvig.claims.config;

import com.hedvig.claims.events.upcast.AutomaticPaymentAddedEvent_v1;
import com.hedvig.claims.events.upcast.AutomaticPaymentAddedEvent_v2;
import com.hedvig.claims.events.upcast.ClaimCreatedEvent_v1;
import com.hedvig.claims.events.upcast.ClaimFileUploadedEventUpcaster_v1;
import com.hedvig.claims.events.upcast.ExpensePaymentAddedEvent_v1;
import com.hedvig.claims.events.upcast.ExpensePaymentAddedEvent_v2;
import com.hedvig.claims.events.upcast.IndemnityCostPaymentAddedEvent_v1;
import com.hedvig.claims.events.upcast.IndemnityCostPaymentAddedEvent_v2;
import com.hedvig.claims.events.upcast.PaymentAddedEvent_v1;
import com.hedvig.claims.events.upcast.PaymentAddedEvent_v2;
import com.hedvig.claims.events.upcast.PaymentAddedEvent_v3;
import com.hedvig.claims.events.upcast.AutomaticPaymentAddedEventUpcaster_v3;
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
            new PaymentAddedEvent_v3(),
            new ClaimFileUploadedEventUpcaster_v1(),
            new AutomaticPaymentAddedEvent_v1(),
            new AutomaticPaymentAddedEvent_v2(),
            new IndemnityCostPaymentAddedEvent_v1(),
            new IndemnityCostPaymentAddedEvent_v2(),
            new ExpensePaymentAddedEvent_v1(),
            new ExpensePaymentAddedEvent_v2(),
            new AutomaticPaymentAddedEventUpcaster_v3()
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
