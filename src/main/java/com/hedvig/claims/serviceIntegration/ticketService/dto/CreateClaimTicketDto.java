package com.hedvig.claims.serviceIntegration.ticketService.dto;

import lombok.Value;

@Value
public class CreateClaimTicketDto {
  private String claimId;
  private String memberId;
  private String description;
}
