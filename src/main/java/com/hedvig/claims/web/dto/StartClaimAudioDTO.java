package com.hedvig.claims.web.dto;

import java.time.Instant;

import lombok.Value;

@Value
public class StartClaimAudioDTO {

	String userId;
	Instant registrationDate;
	String audioURL;

}
