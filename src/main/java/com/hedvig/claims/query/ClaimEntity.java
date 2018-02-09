package com.hedvig.claims.query;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.claims.web.ClaimsController;
import com.hedvig.claims.web.dto.ClaimDTO;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class ClaimEntity {

	private static Logger log = LoggerFactory.getLogger(ClaimEntity.class);
	
    @Id
    public String id;
    public String userId;
    public String name;
    public UUID assetId;
    public String audioURL;
    public LocalDate registrationDate;

    // Not user if this is the right place for this but...
    public ClaimDTO convertToDTO(){
    	log.info("Return dto version of:" + this);
    	return new ClaimDTO(id, name, assetId.toString(), audioURL, registrationDate);
    }
    
}