package com.hedvig.claims.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.time.LocalDateTime;

import lombok.Value;

public class FnolDTO extends HedvigBackofficeDTO{

    public String assetId;
    public String audioURL;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") //TODO: change date format
    public LocalDateTime registrationDate;

    public FnolDTO(){}
    
    public FnolDTO(String id, String name, String assetId, String audioURL, LocalDateTime registrationDate) {
        this.id = id;
        this.userId = name;
        this.registrationDate = registrationDate;
        this.assetId = assetId;
        this.audioURL = audioURL;
    }
    
    public String toString(){
    	return "id:" + this.id + "\n"
    			+ "userId:" + this.userId + "\n"
    			+ "registrationDate:" + this.registrationDate + "\n"
    			+ "assetId:" + this.assetId + "\n"
    			+ "audioURL:" + this.audioURL;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getAudioURL() {
		return audioURL;
	}

	public void setAudioURL(String audioURL) {
		this.audioURL = audioURL;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

}
