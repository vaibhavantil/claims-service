package com.hedvig.claims.aggregates;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.events.ClaimCreatedEvent;
import com.hedvig.claims.events.ClaimStatusUpdatedEvent;
import com.hedvig.claims.events.ClaimsReserveUpdateEvent;
import com.hedvig.claims.events.NoteAddedEvent;
import com.hedvig.claims.events.PaymentAddedEvent;

@Aggregate
public class ClaimsAggregate {

	private static Logger log = LoggerFactory.getLogger(ClaimsAggregate.class);
	public static enum ClaimStates {OPEN, CLOSED, REOPENED};

    @AggregateIdentifier
    public String id;
    public String userId;
    public String audioURL;
    public LocalDateTime registrationDate;
    public ClaimStates state;
    public Double reserve;

    public ArrayList<Payment> payments;
    public ArrayList<Note> notes;
    public ArrayList<String> assets;

    public ClaimsAggregate(){
        log.info("Instansiating ClaimsAggregate");
    }

    @CommandHandler
    public ClaimsAggregate(CreateClaimCommand command) {
        log.info("create claim");
        apply(new ClaimCreatedEvent(command.getId(), command.getUserId(), command.getRegistrationDate(), command.getAudioURL()));
    }

    @CommandHandler
    public void update(UpdateClaimsStateCommand command) {
        log.info("update claim state");
        apply(new ClaimStatusUpdatedEvent(command.getClaimsId(), command.getUserId(), command.getRegistrationDate(), command.getState()));
    }
    
    @CommandHandler
    public void addNote(AddNoteCommand command) {
        log.info("adding note to claim");
        NoteAddedEvent ne = new NoteAddedEvent();
        ne.setClaimsId(command.getClaimID());
        ne.setDate(command.getDate());
        ne.setFileURL(command.getFileURL());
        ne.setId(command.getId());
        ne.setText(command.getText());
        ne.setUserId(command.getUserId());
        apply(ne);
    }
    
    @CommandHandler
    public void addPayment(AddPaymentCommand command) {
        log.info("adding payment to claim");
        PaymentAddedEvent pe = new PaymentAddedEvent();
        pe.setClaimsId(command.getClaimID());
        pe.setDate(command.getDate());
        pe.setId(command.getId());
        pe.setUserId(command.getUserId());
        
        pe.setAmount(command.getAmount());
        pe.setNote(command.getNote());
        pe.setPayoutDate(command.getPayoutDate());
        pe.setExGratia(command.getExGratia());
        apply(pe);
    }

    // ----------------- Event sourcing --------------------- //
    
    @EventSourcingHandler
    public void on(ClaimCreatedEvent e) {
        this.id = e.getId();
        this.userId = e.getUserId();
        this.registrationDate = e.getRegistrationDate();
        this.audioURL = e.getAudioURL();
        
        // Init data structures
        this.notes = new ArrayList<Note>();
        this.payments = new ArrayList<Payment>();
        this.assets = new ArrayList<String>();
    }

    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent e) {
        this.state = e.getState();
    }
    
    @EventSourcingHandler
    public void on(ClaimsReserveUpdateEvent e) {
        this.reserve = e.amount;
    }
    
    @EventSourcingHandler
    public void on(PaymentAddedEvent e) {
    	Payment p = new Payment();
    	p.id = e.getId();
    	p.date = e.getDate();
    	p.userId = e.getUserId();
    	p.amount = e.getAmount();
    	p.payoutDate = e.getPayoutDate();
    	p.note = e.getNote();
    	p.exGratia = e.getExGratia();
    	payments.add(p);
    }
    
    @EventSourcingHandler
    public void on(NoteAddedEvent e) {
    	Note n = new Note();
        n.id = e.getId();
        n.fileURL = e.getFileURL();
        n.text = e.getText();
        n.userId = e.getUserId();
        n.date = e.getDate();
        notes.add(n);
    }
    
}
