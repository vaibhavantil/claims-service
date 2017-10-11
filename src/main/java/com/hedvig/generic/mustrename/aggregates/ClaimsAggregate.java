package com.hedvig.generic.mustrename.aggregates;

import com.hedvig.generic.mustrename.commands.InitiateClaimCommand;
import com.hedvig.generic.mustrename.commands.InitiateClaimForAssetCommand;
import com.hedvig.generic.mustrename.commands.DeleteClaimCommand;
import com.hedvig.generic.mustrename.commands.UpdateClaimCommand;
import com.hedvig.generic.mustrename.events.AssetClaimCreatedEvent;
import com.hedvig.generic.mustrename.events.ClaimCreatedEvent;
import com.hedvig.generic.mustrename.events.ClaimDeletedEvent;
import com.hedvig.generic.mustrename.events.ClaimUpdatedEvent;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * This is an example Aggregate and should be remodeled to suit the needs of you domain.
 */
@Aggregate
public class ClaimsAggregate {

	private static Logger log = LoggerFactory.getLogger(ClaimsAggregate.class);
    @AggregateIdentifier
    public String id;
    public String name;
    public String userId;
    public UUID assetId;
    public LocalDate registrationDate;

    public ClaimsAggregate(){
        log.info("Instansiating ClaimsAggregate");
    }

    @CommandHandler
    public ClaimsAggregate(InitiateClaimCommand command) {
        log.info("create claim");
        apply(new ClaimCreatedEvent(command.getId(), command.getUserId(), command.getRegistrationDate()));
    }

    @CommandHandler
    public ClaimsAggregate(InitiateClaimForAssetCommand command) {
        log.info("create asset claim");
        apply(new AssetClaimCreatedEvent(command.getId(), command.getUserId(), command.getAssetId(), command.getRegistrationDate()));
    }
    
    @CommandHandler
    public void update(UpdateClaimCommand command) {
        log.info("update");
        apply(new ClaimUpdatedEvent(command.getId(), command.getName(), command.getRegistrationDate()));
    }
    
    @CommandHandler
    public void delete(DeleteClaimCommand command) {
        log.info("delete");
        apply(new ClaimDeletedEvent(command.getId()));
    }
    
    @EventSourcingHandler
    public void on(ClaimCreatedEvent e) {
        this.id = e.getId();
        this.userId = e.getUserId();
        this.registrationDate = e.getRegistrationDate();
    }
    
    @EventSourcingHandler
    public void on(AssetClaimCreatedEvent e) {
        this.id = e.getId();
        this.userId = e.getUserId();
        this.assetId = e.getAssetId();
        this.registrationDate = e.getRegistrationDate();
    }
}
