package com.hedvig.claims.events;

import java.time.Instant;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import lombok.Data;

@Data
public class DataItemAddedEvent {

	private String id;
    private String claimsId;
	public Instant date;
	public String userId;
	
	public DataType type;
	public String name;
	public String title;
	public Boolean received;
	public String value;

}
