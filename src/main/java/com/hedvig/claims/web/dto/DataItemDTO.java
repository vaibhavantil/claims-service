package com.hedvig.claims.web.dto;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

import java.time.LocalDateTime;

public class DataItemDTO extends HedvigBackofficeDTO{

	public DataType type;
	public String name;
	public String title;
	public Boolean received;

	public DataItemDTO() { }

	public DataItemDTO(String id, String claimId, LocalDateTime date, String userId, DataType type, String name, String title, Boolean received) {
		this.id = id;
		this.claimID = claimId;
		this.date = date;
		this.userId = userId;
		this.type = type;
		this.name = name;
		this.title = title;
		this.received = received;
	}

}
