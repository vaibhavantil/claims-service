package com.hedvig.claims.web.dto;

import lombok.Value;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Value
public class ClaimReportDTO {
  private UUID claimId;
  private String memberId;
  private LocalDate dateOfLoss;
  private LocalDate notificationDate;
  private Year claimYear;
  private String descriptionOfLoss;
  private MonetaryAmount grossPaid;
  private MonetaryAmount reserved;
  private MonetaryAmount totalIncurred;
  private String claimStatus;
  private LocalDate claimStatusLastUpdated;
}
