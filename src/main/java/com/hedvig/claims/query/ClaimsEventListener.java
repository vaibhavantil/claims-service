package com.hedvig.claims.query;

import com.hedvig.claims.aggregates.*;
import com.hedvig.claims.events.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
        claim.state = ClaimsAggregate.ClaimStates.OPEN.name();
        
        // Init data structures
        claim.notes = new HashSet<Note>();
        claim.payments = new HashSet<Payment>();
        claim.assets = new HashSet<Asset>();
        claim.events = new HashSet<Event>();
        
        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Claim created";
        claim.addEvent(ev);
        
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
        
        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Note added:" + n.text;
        claim.addEvent(ev);
        
        repository.save(claim);
    }

    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent e) {
        ClaimEntity claim = repository.findById(e.getClaimsId()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimsId()));
        
        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Status updated from " + claim.state + " to " + e.getState().toString();
        
        claim.state = e.getState().toString();
        claim.addEvent(ev);
        
        repository.save(claim);
    }
    
    @EventSourcingHandler
    public void on(ClaimsTypeUpdateEvent e) {
        ClaimEntity claim = repository.findById(e.getClaimID()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimID()));
        
        
        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Claim's type " +  (claim.type == null ? "initialised as " : ("updated from " + claim.type  + " to ")) + e.getType();
        
        claim.type = e.getType();
        claim.addEvent(ev);
        
        repository.save(claim);
    }
    
    @EventSourcingHandler
    public void on(ClaimsReserveUpdateEvent e) {
        ClaimEntity claim = repository.findById(e.getClaimID()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimID()));
        
        
        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Reserve updated from " + claim.reserve + " to " + e.getAmount();
        
        claim.reserve = e.getAmount();
        claim.addEvent(ev);
        
        repository.save(claim);
    }
    
    
    @EventSourcingHandler
    public void on(DataItemAddedEvent e) {
        log.info("DattaItemAddedEvent: " + e);
        ClaimEntity claim = repository.findById(e.getClaimsId()).orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + e.getClaimsId()));

        DataItem d = new DataItem();
        d.id = e.getId();
        d.date = e.getDate();
        d.userId = e.getUserId();
        d.name = e.getName();
        d.received = e.getReceived();
        d.title = e.getTitle();
        d.type = e.getType();
        d.value= e.getValue();
        claim.addDataItem(d);

        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Data item added. " + d.name + ":" + (d.received?"":"not ") + "received ";
        claim.addEvent(ev);
        
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

        Event ev = new Event();
        ev.type = e.getClass().getName();
        ev.userId = e.getUserId();
        ev.text = "Payment added. Amount " + p.amount + " with payout date " + p.payoutDate;
        claim.addEvent(ev);
        
        repository.save(claim);
    }
}
