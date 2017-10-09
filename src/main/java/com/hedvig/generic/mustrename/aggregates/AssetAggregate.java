package com.hedvig.generic.mustrename.aggregates;

import com.hedvig.generic.mustrename.commands.CreateAssetCommand;
import com.hedvig.generic.mustrename.commands.DeleteAssetCommand;
import com.hedvig.generic.mustrename.commands.UpdateAssetCommand;
import com.hedvig.generic.mustrename.events.AssetCreatedEvent;
import com.hedvig.generic.mustrename.events.AssetDeletedEvent;
import com.hedvig.generic.mustrename.events.AssetUpdatedEvent;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * This is an example Aggregate and should be remodeled to suit the needs of you domain.
 */
@Aggregate
public class AssetAggregate {

	private static Logger log = LoggerFactory.getLogger(AssetAggregate.class);
    @AggregateIdentifier
    public String id;

    public String name;

    public LocalDate registrationDate;

    public AssetAggregate(){
        log.info("Instansiating AssetAggregate");
    }

    @CommandHandler
    public AssetAggregate(CreateAssetCommand command) {
        log.info("create");
        apply(new AssetCreatedEvent(command.getId(), command.getUserId(), command.getName(), command.getRegistrationDate()));
    }

    @CommandHandler
    public void update(UpdateAssetCommand command) {
        log.info("update");
        apply(new AssetUpdatedEvent(command.getId(), command.getName(), command.getRegistrationDate()));
    }
    
    @CommandHandler
    public void delete(DeleteAssetCommand command) {
        log.info("delete");
        apply(new AssetDeletedEvent(command.getId(), command.getName(), command.getRegistrationDate()));
    }
    
    @EventSourcingHandler
    public void on(AssetCreatedEvent e) {
        this.id = e.getId();
        this.name = e.getName();
        this.registrationDate = e.getRegistrationDate();
    }
}
