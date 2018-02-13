package com.hedvig.claims.events;

import lombok.Data;
import lombok.Value;
import org.axonframework.commandhandling.model.AggregateIdentifier;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DataItemAddedEvent {

	private String id;
    private String claimsId;
	public LocalDateTime date;
	public String userId;
	
	public DataType type;
	public String name;
	public String title;
	public Boolean recieved;

}
