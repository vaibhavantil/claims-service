package com.hedvig.claims.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
public class ClaimReportHistoryEntity {
  @Id
  private UUID claimHistoryId;
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
  private boolean coveringEmployee;
  private Instant timeOfKnowledge;
  private UUID contractId;

  public ClaimReportHistoryEntity(
    String claimId,
    String memberId,
    LocalDate notificationDate,
    LocalDate dateOfLoss,
    String claimStatus,
    boolean coveringEmployee,
    Instant timeOfKnowledge,
    UUID contractId) {
    this.setClaimHistoryId(UUID.randomUUID());
    this.claimId = claimId;
    this.memberId = memberId;
    this.notificationDate = notificationDate;
    this.dateOfLoss = dateOfLoss;
    this.claimStatus = claimStatus;
    this.coveringEmployee = coveringEmployee;
    this.timeOfKnowledge = timeOfKnowledge;
    this.contractId = contractId;
  }

  public static ClaimReportHistoryEntity copy(ClaimReportHistoryEntity old, Instant timeOfKnowledge) {
    return new ClaimReportHistoryEntity(
      UUID.randomUUID(),
      old.getClaimId(),
      old.getMemberId(),
      old.getGrossPaid(),
      old.getReserved(),
      old.getDateOfLoss(),
      old.getNotificationDate(),
      old.getClaimYear(),
      old.getDescriptionOfLoss(),
      old.getCurrency(),
      old.getClaimStatus(),
      old.isCoveringEmployee(),
      timeOfKnowledge,
      old.getContractId()
    );
  }
}
