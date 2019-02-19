package com.hedvig.claims.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Data
@AllArgsConstructor
@Entity
public class ClaimReportEntity {
  @Id
  private String claimId;
  private String memberId;
  private BigDecimal grossPaid;
  private BigDecimal reserved;
  private LocalDate dateOfLoss;
  private LocalDate notificationDate;
  private Integer claimYear;
  private String descriptionOfLoss;
  private String currency;
  private String claimStatus;
  private LocalDate claimStatusLastUpdated;

  public ClaimReportEntity(String claimId, String memberId, LocalDate notificationDate, String claimStatus, LocalDate claimStatusLastUpdated) {
    this.claimId = claimId;
    this.memberId = memberId;
    this.notificationDate = notificationDate;
    this.claimStatus = claimStatus;
    this.claimStatusLastUpdated = claimStatusLastUpdated;
  }
}
