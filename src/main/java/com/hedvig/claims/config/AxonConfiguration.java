package com.hedvig.claims.config;

import com.hedvig.claims.events.upcast.ClaimCreatedEvent_v1;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class AxonConfiguration {
  @Bean
  public JpaEventStorageEngine eventStorageEngine(Serializer serializer,
                                                  DataSource dataSource,
                                                  EventUpcasterChain eventUpcasters,
                                                  EntityManagerProvider entityManagerProvider,
                                                  TransactionManager transactionManager) throws SQLException {
    return new JpaEventStorageEngine(serializer,
      eventUpcasters::upcast,
      dataSource,
      entityManagerProvider,
      transactionManager);
  }

  @Bean
  public EventUpcasterChain eventUpcasters(){
    return new EventUpcasterChain(
      new ClaimCreatedEvent_v1()
    );
  }
}
