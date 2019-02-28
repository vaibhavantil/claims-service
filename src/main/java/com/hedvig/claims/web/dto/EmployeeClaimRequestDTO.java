package com.hedvig.claims.web.dto;

import lombok.Value;

@Value
public class EmployeeClaimRequestDTO {
  private String claimId;
  private boolean coveringEmployee;
}
