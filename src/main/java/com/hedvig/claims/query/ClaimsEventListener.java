package com.hedvig.claims.query;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hedvig.claims.events.AssetClaimCreatedEvent;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimDeletedEvent;
import com.hedvig.claims.events.ClaimUpdatedEvent;

@Component
public class ClaimsEventListener {

	private static Logger log = LoggerFactory.getLogger(ClaimsEventListener.class);
    private final ClaimsRepository repository;

    @Autowired
    public ClaimsEventListener(ClaimsRepository userRepo) {
        this.repository = userRepo;
    }

    @EventHandler
    public void on(ClaimCreatedEvent e){
        log.info("ClaimCreatedEvent: " + e);
        ClaimEntity asset = new ClaimEntity();
        asset.id = e.getId();
        asset.userId = e.getUserId();
        asset.registrationDate = e.getRegistrationDate();
        repository.save(asset);
    }
    
    @EventHandler
    public void on(ClaimUpdatedEvent e){
        log.info("ClaimUpdatedEvent: " + e);
        ClaimEntity asset = repository.findById(e.getId()).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        asset.name = e.getName();
        asset.registrationDate = e.getRegistrationDate();
        repository.save(asset);
    }
    
    @EventHandler
    public void on(ClaimDeletedEvent e){
        log.info("ClaimDeletedEvent: " + e);
        ClaimEntity asset = new ClaimEntity();
        asset.id = e.getId();
        repository.delete(asset);
    }
    
    @EventHandler
    public void on(AssetClaimCreatedEvent e){
        log.info("AssetClaimCreatedEvent: " + e);
        ClaimEntity asset = new ClaimEntity();
        asset.id = e.getId();
        asset.userId = e.getUserId();
        asset.registrationDate = e.getRegistrationDate();
        asset.assetId = e.getAssetId();
        repository.save(asset);
    }
}
