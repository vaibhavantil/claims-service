package com.hedvig.claims.web.dto;

import java.time.LocalDateTime;

public class PaymentDTO extends HedvigBackofficeDTO{

	public Double amount;
	public String note;
	public LocalDateTime payoutDate;
	public Boolean exGratia;
	
}
