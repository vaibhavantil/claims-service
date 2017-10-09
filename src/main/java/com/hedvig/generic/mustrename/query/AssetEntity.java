package com.hedvig.generic.mustrename.query;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.mustrename.web.AssetTrackerController;
import com.hedvig.generic.mustrename.web.dto.AssetDTO;

import java.time.LocalDate;

@Entity
public class AssetEntity {

	private static Logger log = LoggerFactory.getLogger(AssetEntity.class);
	
    @Id
    public String id;

    public String userId;
    
    public String name;

    public LocalDate registrationDate;


    // Not user if this is the right place for this but...
    public AssetDTO convertToDTO(){
    	log.info("Return dto version of:" + this);
    	return new AssetDTO(id, name, registrationDate);
    }
    
}
