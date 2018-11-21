package com.hedvig.claims.web.dto;

import com.hedvig.claims.aggregates.ClaimSource;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateBackofficeClaimDTO {
  String memberId;
  Instant registrationDate;
  ClaimSource claimSource;
}
