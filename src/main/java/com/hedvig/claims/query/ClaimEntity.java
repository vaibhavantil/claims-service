package com.hedvig.claims.query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.aggregates.Asset;
import com.hedvig.claims.aggregates.DataItem;
import com.hedvig.claims.aggregates.Note;
import com.hedvig.claims.aggregates.Payment;

@Entity
public class ClaimEntity {

	private static Logger log = LoggerFactory.getLogger(ClaimEntity.class);
	
    @Id
    public String id;
    public String userId;
    public String audioURL;
    public LocalDateTime registrationDate;
    public String state;
    public String type;
    public Double reserve; 
    
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="claimsId")
	public Set<DataItem> data;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="claimsId")
	public Set<Asset> assets;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="claimsId")
	public Set<Event> events;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="claimsId") 
	public Set<Note> notes;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="claimsId")
	public Set<Payment> payments;
	
	public void addDataItem(DataItem d){
		data.add(d);
	}
	
	public void addEvent(Event e){
		events.add(e);
	}
	
	public void addNote(Note n){
		notes.add(n);
	}
	
	public void addPayment(Payment p){
		payments.add(p);
	}
	
	public void addAsset(Asset a){
		assets.add(a);
	}

}