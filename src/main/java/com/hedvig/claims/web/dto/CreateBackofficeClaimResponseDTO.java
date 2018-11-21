package com.hedvig.claims.web.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class CreateBackofficeClaimResponseDTO {
  UUID claimId;
}
