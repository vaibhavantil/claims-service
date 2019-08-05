package com.hedvig.claims.serviceIntegration.ticketService.dto;

import lombok.Value;

@Value
public class ClaimToTicketDto {
  private String claimId;
  private String memberId;
  private String description;
}
