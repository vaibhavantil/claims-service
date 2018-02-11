package com.hedvig.claims.web.dto;

import com.hedvig.claims.web.dto.ClaimDataType.DataType;

public class ClaimDataDTO extends HedvigBackofficeDTO{

	public DataType type;
	public String name;
	public String title;
	public Boolean recieved;

}
