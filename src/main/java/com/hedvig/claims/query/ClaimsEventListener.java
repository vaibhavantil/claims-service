package com.hedvig.claims.query;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hedvig.claims.aggregates.Note;
import com.hedvig.claims.aggregates.Payment;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.events.PaymentAddedEvent;

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
        ClaimEntity claim = new ClaimEntity();
        claim.id = e.getId();
        claim.userId = e.getUserId();
        claim.registrationDate = e.getRegistrationDate();
        claim.audioURL = e.getAudioURL();
        repository.save(claim);
    }
    
    @EventHandler
    public void on(NoteAddedEvent e){
        log.info("NoteAddedEvent: " + e);
        ClaimEntity claim = repository.findById(e.getClaimsId()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimsId()));
        Note n = new Note();
        n.id = e.getId();
        n.date = e.getDate();
        n.fileURL = e.getFileURL();
        n.text = e.getText();
        n.userId = e.getUserId();
        claim.addNote(n);
        repository.save(claim);
    }

    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent e) {
        ClaimEntity claim = repository.findById(e.getClaimsId()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimsId()));
        claim.state = e.getState().toString();
        repository.save(claim);
    }
    
    @EventSourcingHandler
    public void on(ClaimsReserveUpdateEvent e) {
        ClaimEntity claim = repository.findById(e.getClaimID()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimID()));
        claim.reserve = e.getAmount();
        repository.save(claim);
    }
    
    @EventSourcingHandler
    public void on(PaymentAddedEvent e) {
    	log.info("PaymentAddedEvent: " + e);
    	ClaimEntity claim = repository.findById(e.getClaimsId()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimsId()));
    	Payment p = new Payment();
    	p.id = e.getId();
    	p.date = e.getDate();
    	p.userId = e.getUserId();
    	p.amount = e.getAmount();
    	p.payoutDate = e.getPayoutDate();
    	p.note = e.getNote();
    	p.exGratia = e.getExGratia();
    	claim.addPayment(p);
    	repository.save(claim);
    }
}
