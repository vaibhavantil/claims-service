package com.hedvig.claims.web.dto;

import java.time.Instant;

public class AssetDTO extends HedvigBackofficeDTO{
	
	public String assetId;
	
	public AssetDTO(){}
	
	public AssetDTO(String assetId, String claimsId, Instant registrationDate, String userId){
		this.assetId = assetId;
		this.id = claimsId;
		this.date = registrationDate;
		this.userId = userId;
	}
	
}
