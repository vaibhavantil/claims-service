package com.hedvig.claims.aggregates;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

@Entity
public class DataItem {
	
	@Id
	public String id;
	public Instant date;
	public String userId;
	
	public DataType type;
	public String name;
	public String title;
	public Boolean recieved;
	public String value;
	
}
